package net.crizin.webs;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.httpcomponents.hc5.ObservationExecChainHandler;
import io.micrometer.core.instrument.binder.httpcomponents.hc5.PoolingHttpClientConnectionManagerMetricsBinder;
import net.crizin.webs.exception.WebsException;
import net.crizin.webs.request.DeleteRequestBuilder;
import net.crizin.webs.request.GetRequestBuilder;
import net.crizin.webs.request.HeadRequestBuilder;
import net.crizin.webs.request.OptionsRequestBuilder;
import net.crizin.webs.request.PatchRequestBuilder;
import net.crizin.webs.request.PostRequestBuilder;
import net.crizin.webs.request.PutRequestBuilder;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;

import java.io.Closeable;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public class Webs implements Closeable {

	private final WebsConfig config;
	private final CookieStore cookieStore = new BasicCookieStore();

	private CloseableHttpClient httpClient;

	Webs(WebsConfig config) {
		this.config = config;
		this.httpClient = createHttpClient();
	}

	public WebsConfig getConfig() {
		return config;
	}

	public static WebsBuilder builder() {
		return new WebsBuilder();
	}

	public static Webs createSimple() {
		return builder().build();
	}

	public GetRequestBuilder get(String url) {
		return new GetRequestBuilder(this, url);
	}

	public HeadRequestBuilder head(String url) {
		return new HeadRequestBuilder(this, url);
	}

	public PostRequestBuilder post(String url) {
		return new PostRequestBuilder(this, url);
	}

	public PutRequestBuilder put(String url) {
		return new PutRequestBuilder(this, url);
	}

	public DeleteRequestBuilder delete(String url) {
		return new DeleteRequestBuilder(this, url);
	}

	public OptionsRequestBuilder options(String url) {
		return new OptionsRequestBuilder(this, url);
	}

	public PatchRequestBuilder patch(String url) {
		return new PatchRequestBuilder(this, url);
	}

	public CloseableHttpClient getHttpClient(boolean createNew) {
		if (!createNew) {
			return httpClient;
		}

		try {
			httpClient.close();
		} catch (IOException ignored) {
			// ignored
		}

		httpClient = createHttpClient();
		return httpClient;
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

	public void setCookie(String domain, String name, String value) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(domain);
		cookie.setPath("/");

		getCookieStore().addCookie(cookie);
	}

	@Override
	public void close() {
		try {
			httpClient.close();
		} catch (IOException e) {
			throw new WebsException(e);
		}
	}

	private CloseableHttpClient createHttpClient() {
		PoolingHttpClientConnectionManager connectionManager;

		try {
			connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
				.setDefaultConnectionConfig(ConnectionConfig.custom()
					.setConnectTimeout(config.connectionTimeout)
					.setSocketTimeout(config.readTimeout)
					.build()
				)
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
			.setUserAgent(config.userAgent);

		if (config.disableContentCompression) {
			httpClientsBuilder.disableContentCompression();
		}

		if (config.observationRegistry != null) {
			new PoolingHttpClientConnectionManagerMetricsBinder(connectionManager, "webs-pool")
				.bindTo(Metrics.globalRegistry);

			httpClientsBuilder
				.addExecInterceptorLast("micrometer", new ObservationExecChainHandler(config.observationRegistry));
		}

		return httpClientsBuilder.build();
	}
}
