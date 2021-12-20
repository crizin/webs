package net.crizin.webs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.crizin.webs.exception.WebsResponseException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class Response {

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
			return EntityUtils.toString(httpResponse.getEntity());
		} catch (IOException | ParseException e) {
			throw new WebsResponseException(e);
		} finally {
			try {
				httpResponse.close();
			} catch (IOException ignore) {
			}
		}
	}

	public <T> T as(Class<T> type) {
		try {
			return WebsUtil.fromJson(asString(), type);
		} finally {
			try {
				httpResponse.close();
			} catch (IOException ignore) {
			}
		}
	}

	public <T> T as(TypeReference<T> type) {
		try {
			return WebsUtil.fromJson(asString(), type);
		} finally {
			try {
				httpResponse.close();
			} catch (IOException ignore) {
			}
		}
	}

	public JsonNode asJson() {
		try {
			return WebsUtil.fromJson(asString());
		} finally {
			try {
				httpResponse.close();
			} catch (IOException ignore) {
			}
		}

	}

	public Map<String, Object> asMap() {
		return as(new TypeReference<Map<String, Object>>() {});
	}

	public List<Map<String, Object>> asMapList() {
		return as(new TypeReference<List<Map<String, Object>>>() {});
	}
}