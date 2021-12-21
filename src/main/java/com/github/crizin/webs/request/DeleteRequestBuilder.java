package com.github.crizin.webs.request;

import com.github.crizin.webs.Response;
import com.github.crizin.webs.Webs;
import org.apache.hc.client5.http.classic.methods.HttpDelete;

public class DeleteRequestBuilder extends BaseRequestBuilder<DeleteRequestBuilder> {

	public DeleteRequestBuilder(Webs webs, String url) {
		super(webs, url);
	}

	public Response fetch() {
		return execute(new HttpDelete(getUrl()));
	}
}