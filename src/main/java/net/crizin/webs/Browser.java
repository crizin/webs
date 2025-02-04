package net.crizin.webs;

import java.util.HashMap;
import java.util.Map;

public enum Browser {
	CHROME(
		"accept-language: ko,en-US;q=0.9,en;q=0.8",
		"accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
		"cache-control: max-age=0",
		"priority: u=0, i",
		"sec-ch-ua-mobile: ?0",
		"sec-ch-ua-platform: \"Windows\"",
		"sec-ch-ua: \"Not(A:Brand\";v=\"99\", \"Google Chrome\";v=\"133\", \"Chromium\";v=\"133\"",
		"sec-fetch-dest: document",
		"sec-fetch-mode: navigate",
		"sec-fetch-site: same-origin",
		"sec-fetch-user: ?1",
		"upgrade-insecure-requests: 1",
		"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36"
	),
	EDGE(
		"accept-language: ko,en;q=0.9,en-US;q=0.8,ja;q=0.7,zh-CN;q=0.6,zh;q=0.5",
		"accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
		"cache-control: max-age=0",
		"priority: u=0, i",
		"sec-ch-ua-mobile: ?0",
		"sec-ch-ua-platform: \"Windows\"",
		"sec-ch-ua: \"Not A(Brand\";v=\"8\", \"Chromium\";v=\"132\", \"Microsoft Edge\";v=\"132\"",
		"sec-fetch-dest: document",
		"sec-fetch-mode: navigate",
		"sec-fetch-site: same-origin",
		"sec-fetch-user: ?1",
		"upgrade-insecure-requests: 1",
		"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36 Edg/132.0.0.0"
	),
	FACEBOOK_EXTERNAL_HIT(
		"accept-language: ko,en;q=0.9,en-US;q=0.8",
		"accept: text/html,application/xhtml+xml,application/xml;q=0.9",
		"cache-control: max-age=0",
		"user-agent: facebookexternalhit/1.1 (+http://www.facebook.com/externalhit_uatext.php)"
	);

	private final Map<String, String> headers;

	Browser(String... headers) {
		this.headers = new HashMap<>(headers.length);

		for (String header : headers) {
			var parts = header.split(":", 2);
			this.headers.put(parts[0].strip(), parts[1].strip());
		}
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
}
