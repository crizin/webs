package net.crizin.webs.request;

import net.crizin.webs.Response;
import net.crizin.webs.Webs;
import org.apache.hc.client5.http.classic.methods.HttpPatch;

public class PatchRequestBuilder extends PostRequestBuilder {

	public PatchRequestBuilder(Webs webs, String url) {
		super(webs, url);
	}

	@Override
	public Response fetch() {
		HttpPatch request = new HttpPatch(getUrl());
		setPayload(request);
		return execute(request);
	}
}
