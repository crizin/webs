package net.crizin.webs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WebsGetTest extends AbstractTest {

	@Test
	void testGetString() {
		assertThat(http.get("/get").fetch().asString())
				.hasSizeGreaterThan(100);
	}

	@Test
	void testGetWithQueryParameters() {
		Map<String, String> params = new HashMap<>();
		params.put("a", "1");
		params.put("b", "2");
		params.put(complexString, complexString);

		Data data = http.get("/get")
				.queryParams(params)
				.queryParam("c", 3)
				.queryParam("c", 4)
				.queryParam("c", 3)
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getArgs).extracting("a").isEqualTo("1");
		assertThat(data).extracting(Data::getArgs).extracting("b").isEqualTo("2");
		assertThat(data).extracting(Data::getArgs).extracting(complexString).isEqualTo(complexString);
		assertThat(data).extracting(Data::getArgs).extracting("c").asList().containsExactly("3", "4", "3");
	}

	@Test
	void testGetWithQueryParametersOmitting() {
		Data data = http.get("/get")
				.queryParam("a", null)
				.fetch()
				.as(Data.class);

		assertThat(data.getArgs()).contains(entry("a", ""));

		data = http.get("/get")
				.omitNullQueryParamValue()
				.queryParam("a", null)
				.fetch()
				.as(Data.class);

		assertThat(data.getArgs()).doesNotContainKeys("a");
	}

	@Test
	void testGetWithQueryString() {
		Data data = http.get("/get")
				.queryParamString("a=1&b=2&c=3&c=4&" + WebsUtil.encodeUrl(complexString) + "=" + WebsUtil.encodeUrl(complexString))
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getArgs).extracting("a").isEqualTo("1");
		assertThat(data).extracting(Data::getArgs).extracting("b").isEqualTo("2");
		assertThat(data).extracting(Data::getArgs).extracting("c").asList().containsExactly("3", "4");
		assertThat(data).extracting(Data::getArgs).extracting(complexString).isEqualTo(complexString);
	}

	@Test
	void testGetWithTemplating() {
		Data data = http.get("/get?a={a}&b={b}")
				.bind("a", 1)
				.bind("b", 2)
				.queryParam("c", 3)
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getArgs)
				.hasFieldOrPropertyWithValue("a", "1")
				.hasFieldOrPropertyWithValue("b", "2")
				.hasFieldOrPropertyWithValue("c", "3");
	}
}