package net.crizin.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.util.Timeout;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WebsBuilder {

	private final WebsConfig config = new WebsConfig();

	public WebsBuilder baseUrl(String baseUrl) {
		config.baseUrl = baseUrl;
		return this;
	}

	public WebsBuilder setConnectionTimeout(Duration connectionTimeout) {
		config.connectionTimeout = Timeout.of(connectionTimeout);
		return this;
	}

	public WebsBuilder setReadTimeout(Duration readTimeout) {
		config.readTimeout = Timeout.of(readTimeout);
		return this;
	}

	public WebsBuilder setUserAgent(String userAgentString) {
		config.userAgent = userAgentString;
		return this;
	}

	public WebsBuilder acceptCodes(int... codes) {
		config.acceptCodes = Arrays.stream(codes).boxed().collect(Collectors.toSet());
		return this;
	}

	public WebsBuilder setRequestConfig(RequestConfig requestConfig) {
		config.requestConfig = requestConfig;
		return this;
	}

	public WebsBuilder simulateBrowser(Browser browser) {
		config.simulateBrowser = browser;
		return this;
	}

	public WebsBuilder disableKeepAlive() {
		config.disableKeepAlive = true;
		return this;
	}

	public WebsBuilder disableContentCompression() {
		config.disableContentCompression = true;
		return this;
	}

	public WebsBuilder disableAutoReconnect() {
		config.disableAutoReconnect = true;
		return this;
	}

	public WebsBuilder registerPreHook(BiConsumer<HttpClientContext, HttpUriRequestBase> preHook) {
		config.preHook = preHook;
		return this;
	}

	public WebsBuilder registerPostHook(Consumer<Response> postHook) {
		config.postHook = postHook;
		return this;
	}

	public WebsBuilder registerMetrics(MeterRegistry registry) {
		config.meterRegistry = registry;
		return this;
	}

	public WebsBuilder objectMapper(ObjectMapper objectMapper) {
		config.objectMapper = objectMapper;
		return this;
	}

	public Webs build() {
		return new Webs(config);
	}
}
