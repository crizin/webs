package com.github.crizin.webs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.github.crizin.webs.exception.WebsException;
import com.github.crizin.webs.exception.WebsResponseException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WebsUtil {

	private static final ObjectMapper MAPPER;

	static {
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));

		MAPPER = new ObjectMapper();
		MAPPER.registerModule(javaTimeModule);
		MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	private WebsUtil() {
		throw new UnsupportedOperationException();
	}

	public static String encodeUrl(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new WebsException(e);
		}
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		if (json == null) {
			return null;
		}

		try {
			return MAPPER.readValue(json, clazz);
		} catch (IOException e) {
			throw new WebsResponseException(e);
		}
	}

	public static <T> T fromJson(String json, TypeReference<T> typeReference) {
		if (json == null) {
			return null;
		}

		try {
			return MAPPER.readValue(json, typeReference);
		} catch (IOException e) {
			throw new WebsResponseException(e);
		}
	}

	public static JsonNode fromJson(String json) {
		if (json == null) {
			return null;
		}

		try {
			return MAPPER.readTree(json);
		} catch (JsonProcessingException e) {
			throw new WebsResponseException(e);
		}
	}

	public static String toJson(Object object) {
		try {
			return MAPPER.writeValueAsString(object);
		} catch (IOException e) {
			throw new WebsResponseException(e);
		}
	}
}