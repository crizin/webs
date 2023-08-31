package net.crizin.webs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.crizin.webs.exception.WebsResponseException;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.protocol.RedirectLocations;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Response {

	private static final Logger LOGGER = LoggerFactory.getLogger(Response.class);

	private final HttpClientContext context;
	private final HttpUriRequestBase httpRequest;
	private final ResponseHolder responseHolder;
	private final ObjectMapper objectMapper;

	public Response(HttpClientContext context, HttpUriRequestBase httpRequest, ClassicHttpResponse httpResponse, ObjectMapper objectMapper) throws IOException {
		this.context = context;
		this.httpRequest = httpRequest;
		this.responseHolder = ResponseHolder.from(httpResponse);
		this.objectMapper = objectMapper;
	}

	public int statusCode() {
		return responseHolder.code();
	}

	public HttpUriRequestBase getHttpRequest() {
		return httpRequest;
	}

	public ResponseHolder getHttpResponse() {
		return responseHolder;
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
		return Arrays.stream(responseHolder.headers)
			.filter(header -> header.getName().equalsIgnoreCase(name))
			.map(NameValuePair::getValue)
			.findFirst();
	}

	public List<String> getHeaders(String name) {
		return Arrays.stream(responseHolder.headers)
			.filter(header -> header.getName().equalsIgnoreCase(name))
			.map(NameValuePair::getValue)
			.toList();
	}

	public String asString() {
		return new String(asBytes(), responseHolder.charset());
	}

	public byte[] asBytes() {
		return Objects.requireNonNull(responseHolder.body(), "Response body is empty");
	}

	public <T> T as(Class<T> type) {
		try {
			return objectMapper.readValue(asString(), type);
		} catch (IOException e) {
			throw new WebsResponseException(e);
		}
	}

	public <T> T as(TypeReference<T> type) {
		try {
			return objectMapper.readValue(asString(), type);
		} catch (IOException e) {
			throw new WebsResponseException(e);
		}
	}

	public JsonNode asJson() {
		try {
			return objectMapper.readTree(asString());
		} catch (JsonProcessingException e) {
			throw new WebsResponseException(e);
		}
	}

	public Map<String, Object> asMap() {
		return as(new TypeReference<>() {});
	}

	public List<Map<String, Object>> asMapList() {
		return as(new TypeReference<>() {});
	}

	public record ResponseHolder(
		int code,
		Header[] headers,
		Charset charset,
		byte[] body
	) {

		private static final Pattern CHARSET_PATTERN = Pattern.compile("charset\\s*=[\\s\"']*([\\w-]+)", Pattern.CASE_INSENSITIVE);

		public static ResponseHolder from(ClassicHttpResponse response) throws IOException {
			var entity = response.getEntity();
			byte[] body = (entity == null) ? null : EntityUtils.toByteArray(response.getEntity());
			return new ResponseHolder(response.getCode(), response.getHeaders(), getCharset(response, body), body);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			ResponseHolder that = (ResponseHolder) o;
			return code == that.code && Arrays.equals(headers, that.headers) && Objects.equals(charset, that.charset) && Arrays.equals(body, that.body);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(code, charset);
			result = 31 * result + Arrays.hashCode(headers);
			result = 31 * result + Arrays.hashCode(body);
			return result;
		}

		@Override
		public String toString() {
			return "ResponseHolder[code=%d, headers=%s, charset=%s, body=%s]"
				.formatted(code, Arrays.toString(headers), charset, Arrays.toString(body));
		}

		private static Charset getCharset(ClassicHttpResponse httpResponse, byte[] bytes) {
			return Optional.ofNullable(httpResponse.getFirstHeader("Content-Type"))
				.map(NameValuePair::getValue)
				.flatMap(ResponseHolder::detectCharset)
				.orElseGet(() -> Optional.ofNullable(bytes)
					.map(String::new)
					.flatMap(ResponseHolder::detectCharset)
					.orElse(StandardCharsets.UTF_8)
				);
		}

		private static Optional<Charset> detectCharset(String content) {
			return Optional.ofNullable(content)
				.map(CHARSET_PATTERN::matcher)
				.filter(Matcher::find)
				.map(matcher -> matcher.group(1))
				.map(charset -> charset.equalsIgnoreCase("EUC-KR") ? "MS949" : charset)
				.map(charset -> {
					try {
						return Charset.forName(charset);
					} catch (Exception e) {
						LOGGER.debug(e.getMessage(), e);
						return null;
					}
				});
		}
	}
}
