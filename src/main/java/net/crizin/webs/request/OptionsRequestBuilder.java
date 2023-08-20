package net.crizin.webs.request;

import net.crizin.webs.Response;
import net.crizin.webs.Webs;
import org.apache.hc.client5.http.classic.methods.HttpOptions;

public class OptionsRequestBuilder extends BaseRequestBuilder<OptionsRequestBuilder> {

	public OptionsRequestBuilder(Webs webs, String url) {
		super(webs, url);
	}

	public Response fetch() {
		return execute(new HttpOptions(getUrl()));
	}
}
