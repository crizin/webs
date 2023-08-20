package net.crizin.webs.request;

import net.crizin.webs.Response;
import net.crizin.webs.Webs;
import org.apache.hc.client5.http.classic.methods.HttpHead;

public class HeadRequestBuilder extends BaseRequestBuilder<HeadRequestBuilder> {

	public HeadRequestBuilder(Webs webs, String url) {
		super(webs, url);
	}

	public Response fetch() {
		return execute(new HttpHead(getUrl()));
	}
}
