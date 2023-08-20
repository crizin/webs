package net.crizin.webs.request;

import net.crizin.webs.Response;
import net.crizin.webs.Webs;
import org.apache.hc.client5.http.classic.methods.HttpGet;

public class GetRequestBuilder extends BaseRequestBuilder<GetRequestBuilder> {

	public GetRequestBuilder(Webs webs, String url) {
		super(webs, url);
	}

	public Response fetch() {
		return execute(new HttpGet(getUrl()));
	}
}
