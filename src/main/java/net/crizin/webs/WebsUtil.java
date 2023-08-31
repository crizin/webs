package net.crizin.webs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.crizin.webs.exception.WebsResponseException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WebsUtil {

	private WebsUtil() {
		throw new UnsupportedOperationException();
	}

	public static String encodeUrl(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	public static <T> T fromJson(ObjectMapper objectMapper, String json, Class<T> clazz) {
		if (json == null) {
			return null;
		}

		try {
			return objectMapper.readValue(json, clazz);
		} catch (IOException e) {
			throw new WebsResponseException(e);
		}
	}

	public static <T> T fromJson(ObjectMapper objectMapper, String json, TypeReference<T> typeReference) {
		if (json == null) {
			return null;
		}

		try {
			return objectMapper.readValue(json, typeReference);
		} catch (IOException e) {
			throw new WebsResponseException(e);
		}
	}

	public static JsonNode fromJson(ObjectMapper objectMapper, String json) {
		if (json == null) {
			return null;
		}

		try {
			return objectMapper.readTree(json);
		} catch (JsonProcessingException e) {
			throw new WebsResponseException(e);
		}
	}

	public static String toJson(ObjectMapper objectMapper, Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (IOException e) {
			throw new WebsResponseException(e);
		}
	}
}
