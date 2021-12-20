package net.crizin.webs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.crizin.webs.exception.WebsException;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

public class FormBuilder {

	private boolean omitNullValue;
	private boolean dontEncodeKey;
	private final List<String> names = new ArrayList<>();
	private final List<Object> values = new ArrayList<>();

	public FormBuilder add(String name, Object value) {
		names.add(name);
		values.add(value);
		return this;
	}

	public FormBuilder omitNullValue() {
		omitNullValue = true;
		return this;
	}

	public FormBuilder dontEncodeKey() {
		dontEncodeKey = true;
		return this;
	}

	public boolean hasValue() {
		return !names.isEmpty();
	}

	public String buildAsString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0, length = names.size(); i < length; i++) {
			Object value = values.get(i);

			if (value == null && omitNullValue) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append('&');
			}

			if (dontEncodeKey) {
				sb.append(names.get(i));
			} else {
				sb.append(encodeUrl(names.get(i)));
			}

			sb.append('=');

			if (value != null) {
				sb.append(encodeUrl(String.valueOf(value)));
			}
		}

		return sb.toString();
	}

	public List<NameValuePair> buildAsNameValuePairs() {
		List<NameValuePair> parameters = new ArrayList<>(names.size());

		for (int i = 0, length = names.size(); i < length; i++) {
			Object value = values.get(i);

			if (value == null && omitNullValue) {
				continue;
			}

			parameters.add(new BasicNameValuePair(names.get(i), (value == null) ? "" : String.valueOf(value)));
		}

		return parameters;
	}

	private String encodeUrl(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new WebsException(e);
		}
	}
}