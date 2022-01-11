package com.github.crizin.webs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.crizin.webs.exception.WebsException;
import com.github.crizin.webs.exception.WebsResponseException;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response implements Closeable {

	private static final Logger logger = LoggerFactory.getLogger(Response.class);
	private static final Pattern charsetPattern = Pattern.compile("charset=([\\w-]+)", Pattern.CASE_INSENSITIVE);

	private final CloseableHttpResponse httpResponse;

	public Response(CloseableHttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	public int statusCode() {
		return httpResponse.getCode();
	}

	public Optional<String> getHeader(String name) {
		return Optional.ofNullable(httpResponse.getFirstHeader(name)).map(NameValuePair::getValue);
	}

	public List<String> getHeaders(String name) {
		return Arrays.stream(httpResponse.getHeaders(name)).map(NameValuePair::getValue).collect(Collectors.toList());
	}

	public String asString() {
		try {
			return EntityUtils.toString(httpResponse.getEntity(), getCharset(httpResponse));
		} catch (IOException | ParseException e) {
			throw new WebsResponseException(e);
		} finally {
			try {
				httpResponse.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public <T> T as(Class<T> type) {
		try {
			return WebsUtil.fromJson(asString(), type);
		} finally {
			try {
				httpResponse.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public <T> T as(TypeReference<T> type) {
		try {
			return WebsUtil.fromJson(asString(), type);
		} finally {
			try {
				httpResponse.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public JsonNode asJson() {
		try {
			return WebsUtil.fromJson(asString());
		} finally {
			try {
				httpResponse.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public Map<String, Object> asMap() {
		return as(new TypeReference<Map<String, Object>>() {});
	}

	public List<Map<String, Object>> asMapList() {
		return as(new TypeReference<List<Map<String, Object>>>() {});
	}

	@Override
	public void close() {
		try {
			httpResponse.close();
		} catch (IOException e) {
			throw new WebsException(e);
		}
	}

	private Charset getCharset(CloseableHttpResponse httpResponse) {
		return Optional.ofNullable(httpResponse.getFirstHeader("Content-Type"))
				.map(NameValuePair::getValue)
				.map(charsetPattern::matcher)
				.filter(Matcher::find)
				.map(matcher -> matcher.group(1))
				.map(charset -> {
					try {
						return Charset.forName(charset);
					} catch (Exception e) {
						return null;
					}
				})
				.orElse(StandardCharsets.UTF_8);
	}
}