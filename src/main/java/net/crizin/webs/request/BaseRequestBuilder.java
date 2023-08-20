package net.crizin.webs.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.crizin.webs.ParamsBuilder;
import net.crizin.webs.Response;
import net.crizin.webs.Webs;
import net.crizin.webs.exception.WebsRequestException;
import net.crizin.webs.exception.WebsResponseException;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.utils.Base64;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class BaseRequestBuilder<T extends BaseRequestBuilder<?>> {

	protected final Webs webs;
	protected final List<String> queryParamNames = new ArrayList<>();
	protected final List<Object> queryParamValues = new ArrayList<>();
	protected final List<String> headerNames = new ArrayList<>();
	protected final List<Object> headerValues = new ArrayList<>();
	protected final ParamsBuilder paramsBuilder = new ParamsBuilder();
	protected String url;
	protected String payload;
	protected String queryString;
	protected boolean omitNullQueryParamValue;
	protected ContentType contentType = ContentType.create(ContentType.APPLICATION_FORM_URLENCODED.getMimeType(), StandardCharsets.UTF_8);

	protected BaseRequestBuilder(Webs webs, String url) {
		this.webs = webs;
		this.url = combineUrl(webs.getBaseUrl(), url);
	}

	public abstract Response fetch();

	@SuppressWarnings("unchecked")
	public T queryParams(Map<String, ?> params) {
		if (queryString != null) {
			throw new WebsRequestException("QueryString already set");
		}
		params.forEach(this::queryParam);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T queryParam(String name, Object value) {
		if (queryString != null) {
			throw new WebsRequestException("QueryString already set");
		}
		queryParamNames.add(name);
		queryParamValues.add(value);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T queryParamString(String queryString) {
		if (!queryParamNames.isEmpty()) {
			throw new WebsRequestException("Query parameter already set");
		}
		this.queryString = queryString;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T bind(String name, Object value) {
		url = url.replaceAll(String.format("\\{%s}", Pattern.quote(name)), defaultString(value));
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T header(String name, Object value) {
		headerNames.add(name);
		headerValues.add(value);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T basicAuth(String userName, String password) {
		String value = String.format("Basic %s", Base64.encodeBase64String(String.format("%s:%s", userName, password).getBytes(StandardCharsets.UTF_8)));
		header(HttpHeaders.AUTHORIZATION, value);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T omitNullQueryParamValue() {
		this.omitNullQueryParamValue = true;
		return (T) this;
	}

	protected URI getUrl() {
		URIBuilder builder;

		try {
			builder = new URIBuilder(url);
		} catch (URISyntaxException e) {
			throw new WebsRequestException("Invalid url", e);
		}

		if (queryString != null) {
			try {
				builder = new URIBuilder(String.format("%s?%s", url, queryString));
			} catch (URISyntaxException e) {
				throw new WebsRequestException("Invalid queryString", e);
			}
		} else if (!queryParamNames.isEmpty()) {
			for (int i = 0, length = queryParamNames.size(); i < length; i++) {
				Object value = queryParamValues.get(i);

				if (value == null && omitNullQueryParamValue) {
					continue;
				}

				builder.addParameter(queryParamNames.get(i), (value == null) ? "" : String.valueOf(value));
			}
		}

		try {
			return builder.build();
		} catch (URISyntaxException e) {
			throw new WebsRequestException("Invalid url", e);
		}
	}

	protected String combineUrl(String baseUrl, String url) {
		if (baseUrl == null || baseUrl.isEmpty()) {
			return url;
		} else if (baseUrl.endsWith("/")) {
			return baseUrl + (url.startsWith("/") ? url.substring(1) : url);
		} else {
			return baseUrl + (url.startsWith("/") ? url : "/" + url);
		}
	}

	protected String defaultString(Object object) {
		return (object == null) ? "" : String.valueOf(object);
	}

	protected Response execute(HttpUriRequestBase request) {
		return execute(request, false);
	}

	private Response execute(HttpUriRequestBase request, boolean retrying) {
		request.setConfig(webs.getRequestConfig());
		setHeader(request);

		var httpClient = webs.getHttpClient(retrying);

		try {
			HttpClientContext context = HttpClientContext.create();
			context.setAttribute(HttpClientContext.COOKIE_STORE, webs.getCookieStore());
			webs.getPreHook().accept(context, request);
			return httpClient.execute(request, context, response -> {
				if (!webs.isAcceptCode(response.getCode())) {
					throw new WebsResponseException(String.format("%d %s", response.getCode(), response.getReasonPhrase()));
				}
				var responseHolder = new Response(context, request, response);
				webs.getPostHook().accept(responseHolder);
				return responseHolder;
			});
		} catch (ConnectionRequestTimeoutException e) {
			if (retrying || webs.isDisableAutoReconnect()) {
				throw new WebsResponseException(e);
			}
			return execute(request, true);
		} catch (IOException e) {
			throw new WebsResponseException(e);
		}
	}

	protected void setPayload(HttpUriRequestBase request) {
		if (payload != null) {
			request.setEntity(new StringEntity(payload, contentType));
		} else if (paramsBuilder.hasValue()) {
			request.setEntity(new UrlEncodedFormEntity(paramsBuilder.buildAsNameValuePairs(), StandardCharsets.UTF_8));
		}
	}

	private void setHeader(HttpUriRequestBase request) {
		for (int i = 0, length = headerNames.size(); i < length; i++) {
			request.addHeader(headerNames.get(i), (headerValues.get(i) == null) ? "" : String.valueOf(headerValues.get(i)));
		}

		if (webs.getSimulateBrowser() != null) {
			webs.getSimulateBrowser().getHeaders().forEach((name, value) -> {
				if (!request.containsHeader(name)) {
					request.setHeader(name, value);
				}
			});
			if (!request.containsHeader(HttpHeaders.REFERER)) {
				try {
					request.setHeader(HttpHeaders.REFERER, request.getUri().toString());
				} catch (URISyntaxException e) {
					throw new WebsRequestException(e);
				}
			}
		}

		if (webs.isDisableKeepAlive()) {
			request.setHeader("Connection", "close");
		}
	}

	public String fetchAsString() {
		return fetch().asString();
	}

	public byte[] fetchAsBytes() {
		return fetch().asBytes();
	}

	public <R> R fetchAs(Class<R> type) {
		return fetch().as(type);
	}

	public <R> R fetchAs(TypeReference<R> type) {
		return fetch().as(type);
	}

	public JsonNode fetchAsJson() {
		return fetch().asJson();
	}

	public Map<String, Object> fetchAsMap() {
		return fetch().asMap();
	}

	public List<Map<String, Object>> fetchAsMapList() {
		return fetch().asMapList();
	}
}
