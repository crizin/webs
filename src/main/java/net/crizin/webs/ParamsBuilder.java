package net.crizin.webs;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParamsBuilder {

	private boolean omitNullValue;
	private boolean dontEncodeKey;
	private final List<String> names = new ArrayList<>();
	private final List<Object> values = new ArrayList<>();

	public ParamsBuilder add(String name, Object value) {
		names.add(name);
		values.add(value);
		return this;
	}

	public ParamsBuilder omitNullValue() {
		omitNullValue = true;
		return this;
	}

	public ParamsBuilder dontEncodeKey() {
		dontEncodeKey = true;
		return this;
	}

	public boolean hasValue() {
		if (omitNullValue) {
			return values.stream().anyMatch(Objects::nonNull);
		} else {
			return !names.isEmpty();
		}
	}

	public String buildAsString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0, length = names.size(); i < length; i++) {
			Object value = values.get(i);

			if (omitNullValue && value == null) {
				continue;
			}

			if (!sb.isEmpty()) {
				sb.append('&');
			}

			if (dontEncodeKey) {
				sb.append(names.get(i));
			} else {
				sb.append(WebsUtil.encodeUrl(names.get(i)));
			}

			sb.append('=');

			if (value != null) {
				sb.append(WebsUtil.encodeUrl(String.valueOf(value)));
			}
		}

		return sb.toString();
	}

	public List<NameValuePair> buildAsNameValuePairs() {
		List<NameValuePair> parameters = new ArrayList<>(names.size());

		for (int i = 0, length = names.size(); i < length; i++) {
			Object value = values.get(i);

			if (omitNullValue && value == null) {
				continue;
			}

			parameters.add(new BasicNameValuePair(names.get(i), (value == null) ? "" : String.valueOf(value)));
		}

		return parameters;
	}
}
