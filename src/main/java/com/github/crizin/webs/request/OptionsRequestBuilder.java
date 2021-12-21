package com.github.crizin.webs.request;

import com.github.crizin.webs.Webs;
import com.github.crizin.webs.Response;
import org.apache.hc.client5.http.classic.methods.HttpOptions;

public class OptionsRequestBuilder extends BaseRequestBuilder<OptionsRequestBuilder> {

	public OptionsRequestBuilder(Webs webs, String url) {
		super(webs, url);
	}

	public Response fetch() {
		return execute(new HttpOptions(getUrl()));
	}
}