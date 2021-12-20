package net.crizin.webs.request;

import net.crizin.webs.Webs;

public abstract class BaseRequestBuilder<T extends BaseRequestBuilder<?>> {

	protected final Webs webs;
	protected String url;

	protected BaseRequestBuilder(Webs webs, String url) {
		this.webs = webs;
		this.url = combineUrl(webs.getBaseUrl(), url);
	}

	protected String combineUrl(String baseUrl, String url) {
		if (baseUrl == null || baseUrl.isEmpty()) {
			return url;
		} else if (baseUrl.endsWith("/")) {
			return baseUrl + (url.startsWith("/") ? url.substring(1) : url);
		} else {
			return baseUrl + (url.startsWith("/") ? url : "/" + url);
		}
	}
}