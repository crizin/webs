package net.crizin.webs;

import java.util.Map;

public class Data {

	private String url;
	private String data;
	private Map<String, Object> headers;
	private Map<String, Object> cookies;
	private Map<String, Object> args;
	private Map<String, Object> form;

	public String getUrl() {
		return url;
	}

	public String getData() {
		return data;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public Map<String, Object> getCookies() {
		return cookies;
	}

	public Map<String, Object> getArgs() {
		return args;
	}

	public Map<String, Object> getForm() {
		return form;
	}
}