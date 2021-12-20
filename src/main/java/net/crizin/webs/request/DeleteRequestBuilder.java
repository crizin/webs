package net.crizin.webs.request;

import net.crizin.webs.Webs;
import net.crizin.webs.Response;
import org.apache.hc.client5.http.classic.methods.HttpDelete;

public class DeleteRequestBuilder extends BaseRequestBuilder<DeleteRequestBuilder> {

	public DeleteRequestBuilder(Webs webs, String url) {
		super(webs, url);
	}

	public Response fetch() {
		return execute(new HttpDelete(getUrl()));
	}
}