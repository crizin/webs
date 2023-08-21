package net.crizin.webs;

import com.fasterxml.jackson.core.type.TypeReference;
import net.crizin.webs.exception.WebsResponseException;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
class WebsTest extends AbstractTest {

	@Test
	void testGetJson() {
		var data = webs.get("/get").fetchAs(Data.class);
		assertThat(data).extracting(Data::url).isEqualTo("https://httpbin.org/get");

		data = webs.get("/get").fetchAs(new TypeReference<>() {});
		assertThat(data).extracting(Data::url).isEqualTo("https://httpbin.org/get");
	}

	@Test
	void testParseTwice() {
		var response = webs.get("/get").fetch();
		assertDoesNotThrow(response::asString);
		assertDoesNotThrow(response::asString);
	}

	@Test
	void testParamsBuilder() {
		var builder = new ParamsBuilder()
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
		var http = Webs.createSimple();
		http.close();

		assertThat(catchThrowable(() -> http.get("https://httpbin.org/get").fetch()))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("Connection pool shut down");
	}

	@Test
	void testUserAgent() {
		try (var webs = Webs.createSimple()) {
			assertThat(webs.get("https://httpbin.org/get").fetchAs(Data.class))
				.extracting("headers").extracting("User-Agent")
				.asString().startsWith("Apache-HttpClient/5.2.1");
		}

		try (var webs = Webs.builder().setUserAgent("MyUserAgent").build()) {
			var data = webs.get("https://httpbin.org/get").fetch().as(Data.class);
			assertEquals("MyUserAgent", data.headers().get("User-Agent"));
		}
	}

	@Test
	void testRequestHeader() {
		var data = webs.get("/get")
			.header("a", 1)
			.header("b", 2)
			.header("b", 3)
			.fetch()
			.as(Data.class);

		assertThat(data).extracting(Data::headers)
			.hasFieldOrPropertyWithValue("A", "1")
			.hasFieldOrPropertyWithValue("B", "2,3");

		var response = webs.get("/get").fetch();
		assertThat(response.getHeader("Access-Control-Allow-Origin")).hasValue("*");
		assertThat(response.getHeader("access-control-allow-origin")).hasValue("*");
	}

	@Test
	void testResponseHeader() {
		var response = webs.get("/response-headers")
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
		try (Webs webs = Webs.builder().setConnectionTimeout(Duration.ofMillis(5)).build()) {
			assertThat(catchThrowable(() -> webs.get("https://httpbin.org/get").fetch()))
				.isInstanceOf(WebsResponseException.class)
				.hasCauseInstanceOf(ConnectTimeoutException.class);
		}

		try (Webs http2 = Webs.builder().setReadTimeout(Duration.ofMillis(100)).build()) {
			assertThat(catchThrowable(() -> http2.get("https://httpbin.org/delay/2").fetch()))
				.isInstanceOf(WebsResponseException.class)
				.hasCauseInstanceOf(SocketTimeoutException.class);
		}
	}

	@Test
	void testCookie() {
		try (var webs = Webs.createSimple()) {
			var data = webs.get("https://httpbin.org/cookies/set")
				.queryParam("a", 1)
				.fetch()
				.as(Data.class);

			assertThat(data).extracting(Data::cookies).asInstanceOf(InstanceOfAssertFactories.MAP)
				.hasFieldOrPropertyWithValue("a", "1");

			webs.get("https://httpbin.org/cookies/set")
				.queryParam("a", 2)
				.fetch();

			assertThat(webs.getCookieValue("a")).hasValue("2");
			assertThat(super.webs.getCookieValue("a")).isEmpty();

			data = webs.get("https://httpbin.org/cookies").fetchAs(Data.class);
			assertThat(data).extracting(Data::cookies).asInstanceOf(InstanceOfAssertFactories.MAP)
				.doesNotContainKey("new");

			webs.setCookie("httpbin.org", "test1", "10");
			webs.setCookie("httpbin.com", "test2", "10");
			data = webs.get("https://httpbin.org/cookies").fetchAs(Data.class);

			assertThat(data).extracting(Data::cookies).asInstanceOf(InstanceOfAssertFactories.MAP)
				.hasFieldOrPropertyWithValue("test1", "10")
				.doesNotContainKey("test2");
		}
	}

	@Test
	void testOptions() {
		var response = webs.options("/delete").fetch();
		assertThat(response.getHeader("Allow")).isPresent().asString().contains("OPTIONS").contains("DELETE");
	}

	@Test
	void testHead() {
		var response = webs.head("/get").fetch();
		assertThat(response.statusCode()).isEqualTo(200);
	}

	@Test
	void testPut() {
		var data = webs.put("/put")
			.formValue("a", 1)
			.fetch()
			.as(Data.class);

		assertThat(data).extracting(Data::form).hasFieldOrPropertyWithValue("a", "1");
	}

	@Test
	void testDelete() {
		var data = webs.delete("/delete")
			.queryParam("a", 1)
			.fetch()
			.as(Data.class);

		assertThat(data).extracting(Data::args).hasFieldOrPropertyWithValue("a", "1");
	}

	@Test
	void testPatch() {
		var data = webs.patch("/patch")
			.formValue("a", 1)
			.fetch()
			.as(Data.class);

		assertThat(data).extracting(Data::form).hasFieldOrPropertyWithValue("a", "1");
	}

	@Test
	void testAuth() {
		var response = webs.get("/basic-auth/user/pass")
			.fetch();

		assertThat(response).extracting(Response::statusCode).isEqualTo(401);

		var map = webs.get("/basic-auth/user/pass")
			.basicAuth("user", "pass")
			.fetch()
			.asMap();

		assertThat(map).extracting("authenticated").isEqualTo(true);
	}

	@Test
	void testBrowserSimulate() {
		try (var webs = Webs.builder().simulateBrowser(Browser.CHROME).build()) {
			var data = webs.get("https://httpbin.org/get").fetch().as(Data.class);

			assertThat(data).extracting(Data::headers)
				.extracting("User-Agent")
				.isEqualTo("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
		}
	}

	@Test
	void testEucKr() {
		String response = webs.get("https://www.ppomppu.co.kr/zboard/view.php?id=freeboard&no=7757457").fetchAsString();
		assertThat(response).contains("머싰었습니다");
	}

	@Test
	void testAbnormalSsl() {
		try (var webs = Webs.createSimple()) {
			var response = webs.get("https://intdev.yu.ac.kr/intdev/index.do").fetch();
			assertDoesNotThrow(response::asString);
		}
	}

	@Test
	void testDisableGzip() {
		try (var webs = Webs.builder().disableContentCompression().build()) {
			var response = webs.get("https://www.ddanzi.com/free/718000314").fetch();
			assertDoesNotThrow(response::asString);
		}
	}

	@Test
	void testAcceptCode() {
		try (var webs = Webs.createSimple()) {
			var request = webs.get("https://httpbin.org/status/404");
			assertThatThrownBy(request::fetch).isInstanceOf(WebsResponseException.class).hasMessage("404 NOT FOUND");
		}

		try (var webs = Webs.builder().acceptCodes(200, 404).build()) {
			var request = webs.get("https://httpbin.org/status/404");
			assertDoesNotThrow(request::fetch);
		}
	}

	@Test
	void testRedirect() {
		try (var webs = Webs.createSimple()) {
			var response = webs.get("https://www.daum.net/").fetch();
			assertThat(response.getFinalLocation()).isEqualTo("https://www.daum.net/");
		}

		try (var webs = Webs.createSimple()) {
			var response = webs.get("http://daum.net").fetch();
			assertThat(response.getFinalLocation()).isEqualTo("https://www.daum.net/");
		}
	}

	@Test
	void testBinary() {
		try (var webs = Webs.createSimple()) {
			var bytes = webs.get("https://httpbin.org/image").fetchAsBytes();
			assertThat(bytes).hasSize(8090);
		}
	}

	@Test
	void testHooks() {
		AtomicReference<Boolean> preHookCalled = new AtomicReference<>(false);
		AtomicReference<Boolean> postHookCalled = new AtomicReference<>(false);

		BiConsumer<HttpClientContext, HttpUriRequestBase> preHook = (context, request) -> {
			preHookCalled.set(true);
			assertThat(request).extracting(BasicHttpRequest::getRequestUri).isEqualTo("/get");
		};

		Consumer<Response> postHook = (response) -> {
			postHookCalled.set(true);
			assertThat(response).extracting(Response::statusCode).isEqualTo(200);
		};

		try (var webs = Webs.builder().registerPreHook(preHook).registerPostHook(postHook).build()) {
			webs.get("https://httpbin.org/get").fetch();
		}

		assertThat(preHookCalled.get()).isTrue();
		assertThat(postHookCalled.get()).isTrue();
	}
}
