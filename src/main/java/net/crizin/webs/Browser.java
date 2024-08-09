package net.crizin.webs;

import java.util.HashMap;
import java.util.Map;

public enum Browser {
	CHROME(
		"accept-language: ko,en-US;q=0.9,en;q=0.8",
		"accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
		"cache-control: max-age=0",
		"sec-ch-ua-arch: \"x86\"",
		"sec-ch-ua-bitness: \"64\"",
		"sec-ch-ua-form-factors: \"Desktop\"",
		"sec-ch-ua-full-version-list: \"Not)A;Brand\";v=\"99.0.0.0\", \"Google Chrome\";v=\"127.0.6533.100\", \"Chromium\";v=\"127.0.6533.100\"",
		"sec-ch-ua-full-version: \"127.0.6533.100\"",
		"sec-ch-ua-mobile: ?0",
		"sec-ch-ua-model: \"\"",
		"sec-ch-ua-platform-version: \"15.0.0\"",
		"sec-ch-ua-platform: \"Windows\"",
		"sec-ch-ua-wow64: ?0",
		"sec-ch-ua: \"Not)A;Brand\";v=\"99\", \"Google Chrome\";v=\"127\", \"Chromium\";v=\"127\"",
		"sec-fetch-dest: document",
		"sec-fetch-mode: navigate",
		"sec-fetch-site: same-origin",
		"sec-fetch-user: ?1",
		"service-worker-navigation-preload: true",
		"upgrade-insecure-requests: 1",
		"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36"
	),
	EDGE(
		"accept-language: ko,en;q=0.9,en-US;q=0.8,ja;q=0.7,zh-CN;q=0.6,zh;q=0.5",
		"accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
		"cache-control: max-age=0",
		"priority: u=0, i",
		"sec-ch-ua-arch: \"x86\"",
		"sec-ch-ua-bitness: \"64\"",
		"sec-ch-ua-form-factors: \"Desktop\"",
		"sec-ch-ua-full-version-list: \"Not)A;Brand\";v=\"99.0.0.0\", \"Chromium\";v=\"127.0.6533.89\", \"Google Chrome\";v=\"127.0.6533.89\"",
		"sec-ch-ua-full-version: \"127.0.6533.89\"",
		"sec-ch-ua-mobile: ?0",
		"sec-ch-ua-model: \"\"",
		"sec-ch-ua-platform-version: \"15.0.0\"",
		"sec-ch-ua-platform: \"Windows\"",
		"sec-ch-ua-wow64: ?0",
		"sec-ch-ua: \"Not)A;Brand\";v=\"99\", \"Chromium\";v=\"127\", \"Google Chrome\";v=\"127\"",
		"sec-fetch-dest: document",
		"sec-fetch-mode: navigate",
		"sec-fetch-site: none",
		"sec-fetch-user: ?1",
		"service-worker-navigation-preload: true",
		"upgrade-insecure-requests: 1",
		"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36"
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
