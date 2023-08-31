package net.crizin.webs.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.crizin.webs.Response;
import net.crizin.webs.Webs;
import net.crizin.webs.WebsUtil;
import net.crizin.webs.exception.WebsRequestException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;

public class PostRequestBuilder extends BaseRequestBuilder<PostRequestBuilder> {

	public PostRequestBuilder(Webs webs, String url) {
		super(webs, url);
	}

	public PostRequestBuilder omitNullValue() {
		this.paramsBuilder.omitNullValue();
		return this;
	}

	public PostRequestBuilder formValue(String name, Object value) {
		if (payload != null) {
			throw new WebsRequestException("Payload already set");
		}
		paramsBuilder.add(name, value);
		return this;
	}

	public PostRequestBuilder payload(String paramString) {
		if (paramsBuilder.hasValue()) {
			throw new WebsRequestException("Request parameter already set");
		}
		this.payload = paramString;
		return this;
	}

	public PostRequestBuilder jsonPayload(ObjectMapper objectMapper, Object object) {
		return jsonPayload(WebsUtil.toJson(objectMapper, object));
	}

	public PostRequestBuilder jsonPayload(String payload) {
		if (paramsBuilder.hasValue()) {
			throw new WebsRequestException("Request parameter already set");
		}
		this.payload = payload;
		this.contentType = ContentType.APPLICATION_JSON;
		return this;
	}

	public Response fetch() {
		HttpPost request = new HttpPost(getUrl());
		setPayload(request);
		return execute(request);
	}
}
