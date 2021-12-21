package com.github.crizin.webs.request;

import com.github.crizin.webs.Response;
import com.github.crizin.webs.Webs;
import org.apache.hc.client5.http.classic.methods.HttpGet;

public class GetRequestBuilder extends BaseRequestBuilder<GetRequestBuilder> {

	public GetRequestBuilder(Webs webs, String url) {
		super(webs, url);
	}

	public Response fetch() {
		return execute(new HttpGet(getUrl()));
	}
}