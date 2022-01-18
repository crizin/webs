package com.github.crizin.webs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.crizin.webs.exception.WebsException;
import com.github.crizin.webs.exception.WebsResponseException;
import java.io.Closeable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.protocol.RedirectLocations;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response implements Closeable {

	private static final Logger logger = LoggerFactory.getLogger(Response.class);
	private static final Pattern charsetPattern = Pattern.compile("charset\\s*=\\s*([\\w-]+)", Pattern.CASE_INSENSITIVE);

	private final HttpClientContext context;
	private final HttpUriRequestBase httpRequest;
	private final CloseableHttpResponse httpResponse;

	public Response(HttpClientContext context, HttpUriRequestBase httpRequest, CloseableHttpResponse httpResponse) {
		this.context = context;
		this.httpRequest = httpRequest;
		this.httpResponse = httpResponse;
	}

	public int statusCode() {
		return httpResponse.getCode();
	}

	public HttpUriRequestBase getHttpRequest() {
		return httpRequest;
	}

	public CloseableHttpResponse getHttpResponse() {
		return httpResponse;
	}

	public String getFinalLocation() {
		RedirectLocations locations = context.getRedirectLocations();

		if (locations.size() == 0) {
			try {
				return httpRequest.getUri().toString();
			} catch (URISyntaxException e) {
				throw new WebsResponseException(e);
			}
		} else {
			return locations.get(locations.size() - 1).toString();
		}
	}

	public Optional<String> getHeader(String name) {
		return Optional.ofNullable(httpResponse.getFirstHeader(name)).map(NameValuePair::getValue);
	}

	public List<String> getHeaders(String name) {
		return Arrays.stream(httpResponse.getHeaders(name)).map(NameValuePair::getValue).collect(Collectors.toList());
	}

	public String asString() {
		try {
			byte[] bytes = EntityUtils.toByteArray(httpResponse.getEntity());
			return new String(bytes, getCharset(httpResponse, bytes));
		} catch (IOException e) {
			throw new WebsResponseException(e);
		} finally {
			try {
				httpResponse.close();
			} catch (IOException e) {
				logger.debug(e.getMessage(), e);
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
				logger.debug(e.getMessage(), e);
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
				logger.debug(e.getMessage(), e);
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
				logger.debug(e.getMessage(), e);
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

	private Charset getCharset(CloseableHttpResponse httpResponse, byte[] bytes) {
		Charset charset = Optional.ofNullable(httpResponse.getFirstHeader("Content-Type"))
				.map(NameValuePair::getValue)
				.map(charsetPattern::matcher)
				.filter(Matcher::find)
				.map(matcher -> matcher.group(1))
				.map(name -> {
					try {
						return Charset.forName(name);
					} catch (Exception e) {
						return null;
					}
				})
				.orElse(null);

		if (charset == null) {
			Matcher matcher = charsetPattern.matcher(new String(bytes));
			if (matcher.find()) {
				try {
					return Charset.forName(matcher.group(1));
				} catch (Exception e) {
					logger.debug(e.getMessage(), e);
				}
			}
		}

		return (charset == null) ? StandardCharsets.UTF_8 : charset;
	}
}