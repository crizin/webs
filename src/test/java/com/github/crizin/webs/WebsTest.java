package com.github.crizin.webs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.crizin.webs.exception.WebsResponseException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Map;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class WebsTest extends AbstractTest {

	@Test
	void testGetJson() {
		Data data = webs.get("/get").fetch().as(Data.class);
		assertThat(data).extracting(Data::getUrl).isEqualTo("https://httpbin.org/get");

		data = webs.get("/get").fetch().as(new TypeReference<Data>() {});
		assertThat(data).extracting(Data::getUrl).isEqualTo("https://httpbin.org/get");
	}

	@Test
	void testParseTwice() {
		Response response = webs.get("/get").fetch();
		assertDoesNotThrow(response::asString);
		assertThatThrownBy(response::asString).isInstanceOf(WebsResponseException.class);
	}

	@Test
	void testParamsBuilder() {
		ParamsBuilder builder = new ParamsBuilder()
				.add("a[]", 1)
				.add("a[]", 2);
		assertThat(builder.buildAsString()).isEqualTo("a%5B%5D=1&a%5B%5D=2");

		builder = new ParamsBuilder()
				.dontEncodeKey()
				.add("a[]", 1)
				.add("a[]", 2);
		assertThat(builder.buildAsString()).isEqualTo("a[]=1&a[]=2");
	}

	@Test
	void testCloseable() {
		Webs http = Webs.createSimple();
		http.close();

		assertThatThrownBy(() -> http.get("https://httpbin.org/get").fetch())
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Connection pool shut down");

		try (Webs h = Webs.createSimple()) {
			h.get("https://httpbin.org/get").fetch().asString();
		}
	}

	@Test
	void testUserAgent() {
		assertThat(Webs.createSimple().get("https://httpbin.org/get").fetch().as(Data.class))
				.extracting("headers").extracting("User-Agent").asString()
				.startsWith("Apache-HttpClient/5.1.2");

		Webs http = Webs.builder()
				.setUserAgent("MyUserAgent")
				.build();

		Data data = http.get("https://httpbin.org/get").fetch().as(Data.class);
		assertEquals("MyUserAgent", data.getHeaders().get("User-Agent"));
	}

	@Test
	void testRequestHeader() {
		Data data = webs.get("/get")
				.header("a", 1)
				.header("b", 2)
				.header("b", 3)
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getHeaders)
				.hasFieldOrPropertyWithValue("A", "1")
				.hasFieldOrPropertyWithValue("B", "2,3");

		Response response = webs.get("/get").fetch();
		assertThat(response.getHeader("Access-Control-Allow-Origin")).hasValue("*");
		assertThat(response.getHeader("access-control-allow-origin")).hasValue("*");
	}

	@Test
	void testResponseHeader() {
		Response response = webs.get("/response-headers")
				.queryParam("a", 1)
				.queryParam("b", 2)
				.queryParam("b", 3)
				.fetch();

		assertThat(response.getHeader("a")).hasValue("1");
		assertThat(response.getHeader("b")).hasValue("2");
		assertThat(response.getHeader("c")).isEmpty();
		assertThat(response.getHeaders("a")).containsExactly("1");
		assertThat(response.getHeaders("b")).containsExactly("2", "3");
		assertThat(response.getHeaders("c")).isEmpty();
	}

	@Test
	void testTimeout() {
		Webs http1 = Webs.builder()
				.setConnectionTimeout(Duration.ofMillis(1))
				.build();

		assertThatThrownBy(() -> http1.get("https://httpbin.org/get").fetch())
				.isInstanceOf(WebsResponseException.class)
				.hasCauseInstanceOf(ConnectTimeoutException.class);

		Webs http2 = Webs.builder()
				.setReadTimeout(Duration.ofMillis(100))
				.build();

		assertThatThrownBy(() -> http2.get("https://httpbin.org/delay/2").fetch())
				.isInstanceOf(WebsResponseException.class)
				.hasCauseInstanceOf(SocketTimeoutException.class);
	}

	@Test
	void testCookie() {
		Webs http = Webs.createSimple();
		Data data = http.get("https://httpbin.org/cookies/set")
				.queryParam("a", 1)
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getCookies).asInstanceOf(InstanceOfAssertFactories.MAP)
				.hasFieldOrPropertyWithValue("a", "1");

		http.get("https://httpbin.org/cookies/set")
				.queryParam("a", 2)
				.fetch();

		assertThat(http.getCookieValue("a")).hasValue("2");
		assertThat(super.webs.getCookieValue("a")).isEmpty();
	}

	@Test
	void testOptions() {
		Response response = webs.options("/delete").fetch();
		assertThat(response.getHeader("Allow")).isPresent().asString().contains("OPTIONS").contains("DELETE");
	}

	@Test
	void testHead() {
		Response response = webs.head("/get").fetch();
		assertThat(response.statusCode()).isEqualTo(200);
	}

	@Test
	void testPut() {
		Data data = webs.put("/put")
				.formValue("a", 1)
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getForm).hasFieldOrPropertyWithValue("a", "1");
	}

	@Test
	void testDelete() {
		Data data = webs.delete("/delete")
				.queryParam("a", 1)
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getArgs).hasFieldOrPropertyWithValue("a", "1");
	}

	@Test
	void testPatch() {
		Data data = webs.patch("/patch")
				.formValue("a", 1)
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getForm).hasFieldOrPropertyWithValue("a", "1");
	}

	@Test
	void testAuth() {
		Response response = webs.get("/basic-auth/user/pass")
				.fetch();

		assertThat(response).extracting(Response::statusCode).isEqualTo(401);

		Map<String, Object> map = webs.get("/basic-auth/user/pass")
				.basicAuth("user", "pass")
				.fetch()
				.asMap();

		assertThat(map).extracting("authenticated").isEqualTo(true);
	}

	@Test
	void testBrowserSimulate() {
		Data data = Webs.builder()
				.simulateBrowser(Browser.CHROME)
				.build()
				.get("https://httpbin.org/get")
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getHeaders)
				.extracting("User-Agent")
				.isEqualTo("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36");
	}
}