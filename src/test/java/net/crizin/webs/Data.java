package net.crizin.webs;

import java.util.Map;

public record Data(
	String url,
	String data,
	Map<String, Object> headers,
	Map<String, Object> cookies,
	Map<String, Object> args,
	Map<String, Object> form
) {
}
