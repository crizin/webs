package net.crizin.webs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.util.Timeout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WebsConfig {

	private static final Set<Integer> DEFAULT_ACCEPT_CODES = new HashSet<>();

	private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();

	static {
		DEFAULT_ACCEPT_CODES.add(HttpStatus.SC_OK);
		DEFAULT_ACCEPT_CODES.add(HttpStatus.SC_CREATED);
		DEFAULT_ACCEPT_CODES.add(HttpStatus.SC_ACCEPTED);
		DEFAULT_ACCEPT_CODES.add(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION);
		DEFAULT_ACCEPT_CODES.add(HttpStatus.SC_NO_CONTENT);
		DEFAULT_ACCEPT_CODES.add(HttpStatus.SC_RESET_CONTENT);
		DEFAULT_ACCEPT_CODES.add(HttpStatus.SC_PARTIAL_CONTENT);
		DEFAULT_ACCEPT_CODES.add(HttpStatus.SC_MULTI_STATUS);
		DEFAULT_ACCEPT_CODES.add(HttpStatus.SC_ALREADY_REPORTED);
		DEFAULT_ACCEPT_CODES.add(HttpStatus.SC_IM_USED);

		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));

		DEFAULT_OBJECT_MAPPER.registerModule(javaTimeModule);
		DEFAULT_OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		DEFAULT_OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		DEFAULT_OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	String baseUrl;
	RequestConfig requestConfig;
	Browser simulateBrowser;
	String userAgent;
	boolean disableKeepAlive;
	boolean disableAutoReconnect;
	boolean disableContentCompression;
	Set<Integer> acceptCodes = DEFAULT_ACCEPT_CODES;
	Timeout connectionTimeout;
	Timeout readTimeout;
	BiConsumer<HttpClientContext, HttpUriRequestBase> preHook;
	Consumer<Response> postHook;
	ObjectMapper objectMapper;
	MeterRegistry meterRegistry;

	public String getBaseUrl() {
		return baseUrl;
	}

	public RequestConfig getRequestConfig() {
		if (requestConfig != null) {
			return requestConfig;
		}

		return RequestConfig.custom()
			.setCookieSpec(StandardCookieSpec.RELAXED)
			.setExpectContinueEnabled(true)
			.setTargetPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.NTLM, StandardAuthScheme.DIGEST))
			.setProxyPreferredAuthSchemes(Collections.singletonList(StandardAuthScheme.BASIC))
			.setConnectionRequestTimeout(connectionTimeout)
			.setResponseTimeout(readTimeout)
			.setMaxRedirects(5)
			.build();
	}

	public Browser getSimulateBrowser() {
		return simulateBrowser;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public boolean isDisableKeepAlive() {
		return disableKeepAlive;
	}

	public boolean isDisableAutoReconnect() {
		return disableAutoReconnect;
	}

	public boolean isDisableContentCompression() {
		return disableContentCompression;
	}

	public Set<Integer> getAcceptCodes() {
		return acceptCodes;
	}

	public Timeout getConnectionTimeout() {
		return connectionTimeout;
	}

	public Timeout getReadTimeout() {
		return readTimeout;
	}

	public BiConsumer<HttpClientContext, HttpUriRequestBase> getPreHook() {
		return preHook;
	}

	public Consumer<Response> getPostHook() {
		return postHook;
	}

	public ObjectMapper getObjectMapper() {
		return (objectMapper == null) ? DEFAULT_OBJECT_MAPPER : objectMapper;
	}

	public MeterRegistry getMeterRegistry() {
		return meterRegistry;
	}
}
