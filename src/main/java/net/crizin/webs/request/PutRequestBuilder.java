package net.crizin.webs.request;

import net.crizin.webs.Response;
import net.crizin.webs.Webs;
import org.apache.hc.client5.http.classic.methods.HttpPut;

public class PutRequestBuilder extends PostRequestBuilder {

	public PutRequestBuilder(Webs webs, String url) {
		super(webs, url);
	}

	@Override
	public Response fetch() {
		HttpPut request = new HttpPut(getUrl());
		setPayload(request);
		return execute(request);
	}
}
