package com.github.crizin.webs;

import java.util.HashMap;
import java.util.Map;

public enum Browser {
	CHROME(new String[][]{
			{"Cache-Control", "max-age=0"},
			{"Sec-Ch-Ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\""},
			{"Sec-Ch-Ua-Mobile", "?0"},
			{"Sec-Ch-Ua-Platform", "\"Windows\""},
			{"Upgrade-Insecure-Requests", "1"},
			{"User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36"},
			{"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"},
			{"Sec-Fetch-Site", "same-origin"},
			{"Sec-Fetch-Mode", "navigate"},
			{"Sec-Fetch-User", "?1"},
			{"Sec-Fetch-Dest", "document"},
			{"Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8,en-US;q=0.7"}
	}),
	MOBILE_CHROME(new String[][]{
			{"Cache-Control", "max-age=0"},
			{"Sec-Ch-Ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\""},
			{"Sec-Ch-Ua-Mobile", "?1"},
			{"Sec-Ch-Ua-Platform", "\"Android\""},
			{"Upgrade-Insecure-Requests", "1"},
			{"User-Agent", "Mozilla/5.0 (Linux; Android 12; SM-G998U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Mobile Safari/537.36"},
			{"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"},
			{"Sec-Fetch-Site", "same-origin"},
			{"Sec-Fetch-Mode", "navigate"},
			{"Sec-Fetch-User", "?1"},
			{"Sec-Fetch-Dest", "document"},
			{"Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8,en-US;q=0.7"}
	}),
	SAFARI(new String[][]{
			{"Cache-Control", "max-age=0"},
			{"User-Agent", "Mozilla/5.0 (MacBook Air; M1 Mac OS X 11_4) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/604.1"},
			{"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"},
			{"Accept-Language", "ko-KR,ko;q=0.9"}
	}),
	EDGE(new String[][]{
			{"Cache-Control", "max-age=0"},
			{"Sec-Ch-Ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Microsoft Edge\";v=\"96\""},
			{"Sec-Ch-Ua-Mobile", "?0"},
			{"Sec-Ch-Ua-Platform", "\"Windows\""},
			{"Upgrade-Insecure-Requests", "1"},
			{"User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36 Edg/96.0.1054.53"},
			{"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"},
			{"Sec-Fetch-Site", "none"},
			{"Sec-Fetch-Mode", "navigate"},
			{"Sec-Fetch-User", "?1"},
			{"Sec-Fetch-Dest", "document"},
			{"Accept-Language", "ko,en;q=0.9,en-US;q=0.8"}
	});

	private final Map<String, String> headers;

	Browser(String[][] headers) {
		this.headers = new HashMap<>(headers.length);

		for (String[] header : headers) {
			this.headers.put(header[0], header[1]);
		}
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
}