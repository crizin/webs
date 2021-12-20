package net.crizin.webs;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.crizin.webs.exception.WebsException;
import net.crizin.webs.request.DeleteRequestBuilder;
import net.crizin.webs.request.GetRequestBuilder;
import net.crizin.webs.request.HeadRequestBuilder;
import net.crizin.webs.request.OptionsRequestBuilder;
import net.crizin.webs.request.PatchRequestBuilder;
import net.crizin.webs.request.PostRequestBuilder;
import net.crizin.webs.request.PutRequestBuilder;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.util.Timeout;

public class Webs implements Closeable {

	private final String baseUrl;
	private final CloseableHttpClient httpClient;
	private final HttpClientContext httpClientContext;
	private final RequestConfig requestConfig;
	private final Browser simulateBrowser;

	private Webs(HttpBuilder builder) {
		this.baseUrl = builder.baseUrl;
		this.httpClient = (builder.client == null) ? HttpClients.custom()
				.setUserAgent(builder.userAgent)
				.build() : builder.client;
		this.httpClientContext = HttpClientContext.create();
		this.httpClientContext.setAttribute(HttpClientContext.COOKIE_STORE, new BasicCookieStore());
		this.requestConfig = (builder.requestConfig == null) ? RequestConfig.custom()
				.setCookieSpec(StandardCookieSpec.STRICT)
				.setTargetPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.NTLM, StandardAuthScheme.DIGEST))
				.setProxyPreferredAuthSchemes(Collections.singletonList(StandardAuthScheme.BASIC))
				.setConnectTimeout(Timeout.ofNanoseconds(builder.connectionTimeout.toNanos()))
				.setConnectionRequestTimeout(Timeout.ofNanoseconds(builder.connectionTimeout.toNanos()))
				.setResponseTimeout(Timeout.ofNanoseconds(builder.readTimeout.toNanos()))
				.build() : builder.requestConfig;
		this.simulateBrowser = builder.simulateBrowser;
	}

	public static HttpBuilder builder() {
		return new HttpBuilder();
	}

	public static Webs createSimple() {
		return Webs.builder().build();
	}

	public static FormBuilder formBuilder() {
		return new FormBuilder();
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

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public HttpClientContext getHttpClientContext() {
		return httpClientContext;
	}

	public RequestConfig getRequestConfig() {
		return requestConfig;
	}

	public List<Cookie> getCookies() {
		return httpClientContext.getCookieStore().getCookies();
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
		private Duration connectionTimeout = Duration.ofSeconds(5);
		private Duration readTimeout = Duration.ofSeconds(60);
		private CloseableHttpClient client;
		private RequestConfig requestConfig;
		private Browser simulateBrowser;

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

		public HttpBuilder setClient(CloseableHttpClient client) {
			this.client = client;
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

		public Webs build() {
			return new Webs(this);
		}
	}
}