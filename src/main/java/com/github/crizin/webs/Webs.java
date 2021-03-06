package com.github.crizin.webs;

import com.github.crizin.webs.exception.WebsException;
import com.github.crizin.webs.request.DeleteRequestBuilder;
import com.github.crizin.webs.request.GetRequestBuilder;
import com.github.crizin.webs.request.HeadRequestBuilder;
import com.github.crizin.webs.request.OptionsRequestBuilder;
import com.github.crizin.webs.request.PatchRequestBuilder;
import com.github.crizin.webs.request.PostRequestBuilder;
import com.github.crizin.webs.request.PutRequestBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

public class Webs implements Closeable {

	private static final Set<Integer> DEFAULT_ACCEPT_CODES = new HashSet<Integer>() {
		{
			add(HttpStatus.SC_OK);
			add(HttpStatus.SC_CREATED);
			add(HttpStatus.SC_ACCEPTED);
			add(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION);
			add(HttpStatus.SC_NO_CONTENT);
			add(HttpStatus.SC_RESET_CONTENT);
			add(HttpStatus.SC_PARTIAL_CONTENT);
			add(HttpStatus.SC_MULTI_STATUS);
			add(HttpStatus.SC_ALREADY_REPORTED);
			add(HttpStatus.SC_IM_USED);
		}
	};

	private final String baseUrl;
	private final CookieStore cookieStore = new BasicCookieStore();
	private final RequestConfig requestConfig;
	private final Browser simulateBrowser;
	private final String userAgent;
	private final boolean disableKeepAlive;
	private final boolean disableAutoReconnect;
	private final boolean disableContentCompression;
	private final Set<Integer> acceptCodes;
	private CloseableHttpClient httpClient;

	private Webs(HttpBuilder builder) {
		this.baseUrl = builder.baseUrl;
		this.userAgent = builder.userAgent;
		this.simulateBrowser = builder.simulateBrowser;
		this.disableKeepAlive = builder.disableKeepAlive;
		this.disableAutoReconnect = builder.disableAutoReconnect;
		this.disableContentCompression = builder.disableContentCompression;
		this.acceptCodes = (builder.acceptCodes == null) ? DEFAULT_ACCEPT_CODES : builder.acceptCodes;
		this.requestConfig = (builder.requestConfig == null) ? RequestConfig.custom()
				.setCookieSpec(StandardCookieSpec.RELAXED)
				.setExpectContinueEnabled(true)
				.setTargetPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.NTLM, StandardAuthScheme.DIGEST))
				.setProxyPreferredAuthSchemes(Collections.singletonList(StandardAuthScheme.BASIC))
				.setConnectTimeout(Timeout.ofNanoseconds(builder.connectionTimeout.toNanos()))
				.setConnectionRequestTimeout(Timeout.ofNanoseconds(builder.connectionTimeout.toNanos()))
				.setResponseTimeout(Timeout.ofNanoseconds(builder.readTimeout.toNanos()))
				.build() : builder.requestConfig;

		this.httpClient = createHttpClient();
	}

	private CloseableHttpClient createHttpClient() {
		PoolingHttpClientConnectionManager connectionManager;

		try {
			connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
					.setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
							.setSslContext(SSLContextBuilder.create().loadTrustMaterial(TrustAllStrategy.INSTANCE).build())
							.setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
							.build()
					)
					.build();
		} catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
			throw new WebsException(e);
		}

		HttpClientBuilder httpClientsBuilder = HttpClients.custom()
				.evictIdleConnections(TimeValue.ofSeconds(10))
				.setConnectionManager(connectionManager)
				.setUserAgent(userAgent);

		if (disableContentCompression) {
			httpClientsBuilder.disableContentCompression();
		}

		return httpClientsBuilder.build();
	}

	public static HttpBuilder builder() {
		return new HttpBuilder();
	}

	public static Webs createSimple() {
		return Webs.builder().build();
	}

	public OptionsRequestBuilder options(String url) {
		return new OptionsRequestBuilder(this, url);
	}

	public GetRequestBuilder get(String url) {
		return new GetRequestBuilder(this, url);
	}

	public HeadRequestBuilder head(String url) {
		return new HeadRequestBuilder(this, url);
	}

	public PutRequestBuilder put(String url) {
		return new PutRequestBuilder(this, url);
	}

	public PostRequestBuilder post(String url) {
		return new PostRequestBuilder(this, url);
	}

	public DeleteRequestBuilder delete(String url) {
		return new DeleteRequestBuilder(this, url);
	}

	public PatchRequestBuilder patch(String url) {
		return new PatchRequestBuilder(this, url);
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public CloseableHttpClient getHttpClient(boolean createNew) {
		if (!createNew) {
			return httpClient;
		}

		try {
			httpClient.close();
		} catch (IOException ignored) {
		}

		this.httpClient = createHttpClient();
		return this.httpClient;
	}

	public RequestConfig getRequestConfig() {
		return requestConfig;
	}

	public boolean isAcceptCode(int statusCode) {
		return acceptCodes.contains(statusCode);
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	public List<Cookie> getCookies() {
		return getCookieStore().getCookies();
	}

	public Optional<Cookie> getCookie(String name) {
		return getCookies().stream()
				.filter(cookie -> cookie.getName().equals(name))
				.findFirst();
	}

	public Optional<String> getCookieValue(String name) {
		return getCookie(name).map(Cookie::getValue);
	}

	public Browser getSimulateBrowser() {
		return simulateBrowser;
	}

	public boolean isDisableKeepAlive() {
		return disableKeepAlive;
	}

	public boolean isDisableAutoReconnect() {
		return disableAutoReconnect;
	}

	@Override
	public void close() {
		try {
			httpClient.close();
		} catch (IOException e) {
			throw new WebsException(e);
		}
	}

	public static class HttpBuilder {

		private String baseUrl = "";
		private String userAgent;
		private Set<Integer> acceptCodes;
		private Duration connectionTimeout = Duration.ofSeconds(5);
		private Duration readTimeout = Duration.ofSeconds(60);
		private RequestConfig requestConfig;
		private Browser simulateBrowser;
		private boolean disableKeepAlive;
		private boolean disableContentCompression;
		private boolean disableAutoReconnect;

		public HttpBuilder baseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
			return this;
		}

		public HttpBuilder setConnectionTimeout(Duration connectionTimeout) {
			this.connectionTimeout = connectionTimeout;
			return this;
		}

		public HttpBuilder setReadTimeout(Duration readTimeout) {
			this.readTimeout = readTimeout;
			return this;
		}

		public HttpBuilder setUserAgent(String userAgentString) {
			this.userAgent = userAgentString;
			return this;
		}

		public HttpBuilder acceptCodes(int... codes) {
			this.acceptCodes = Arrays.stream(codes).boxed().collect(Collectors.toSet());
			return this;
		}

		public HttpBuilder setRequestConfig(RequestConfig requestConfig) {
			this.requestConfig = requestConfig;
			return this;
		}

		public HttpBuilder simulateBrowser(Browser browser) {
			this.simulateBrowser = browser;
			return this;
		}

		public HttpBuilder disableKeepAlive() {
			this.disableKeepAlive = true;
			return this;
		}

		public HttpBuilder disableContentCompression() {
			this.disableContentCompression = true;
			return this;
		}

		public HttpBuilder disableAutoReconnect() {
			this.disableAutoReconnect = true;
			return this;
		}

		public Webs build() {
			return new Webs(this);
		}
	}
}