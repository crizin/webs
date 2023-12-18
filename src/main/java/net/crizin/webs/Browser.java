package net.crizin.webs;

import java.util.HashMap;
import java.util.Map;

public enum Browser {
	CHROME(
		"Cache-Control: max-age=0",
		"sec-ch-ua: \"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"",
		"sec-ch-ua-mobile: ?0",
		"sec-ch-ua-platform: \"Windows\"",
		"Upgrade-Insecure-Requests: 1",
		"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
		"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
		"Sec-Fetch-Site: none",
		"Sec-Fetch-Mode: navigate",
		"Sec-Fetch-User: ?1",
		"Sec-Fetch-Dest: document",
		"Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7"
	),
	EDGE(
		"Cache-Control: max-age=0",
		"sec-ch-ua: \"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Microsoft Edge\";v=\"120\"",
		"sec-ch-ua-mobile: ?0",
		"sec-ch-ua-platform: \"Windows\"",
		"Upgrade-Insecure-Requests: 1",
		"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0",
		"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
		"Sec-Fetch-Site: none",
		"Sec-Fetch-Mode: navigate",
		"Sec-Fetch-User: ?1",
		"Sec-Fetch-Dest: document",
		"Accept-Language: ko,en;q=0.9,en-US;q=0.8"
	);

	private final Map<String, String> headers;

	Browser(String... headers) {
		this.headers = new HashMap<>(headers.length);

		for (String header : headers) {
			var parts = header.split(":", 2);
			this.headers.put(parts[0].trim(), parts[1].trim());
		}
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
}
