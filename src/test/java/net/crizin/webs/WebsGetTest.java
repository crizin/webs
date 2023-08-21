package net.crizin.webs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

@Disabled
class WebsGetTest extends AbstractTest {

	@Test
	void testGetString() {
		var response = webs.get("/get").fetchAsString();
		assertThat(response).hasSizeGreaterThan(100);
	}

	@Test
	void testGetWithQueryParameters() {
		var params = new HashMap<String, Object>();
		params.put("a", "1");
		params.put("b", "2");
		params.put(COMPLEX_STRING, COMPLEX_STRING);

		var data = webs.get("/get")
			.queryParams(params)
			.queryParam("c", 3)
			.queryParam("c", 4)
			.queryParam("c", 3)
			.fetchAs(Data.class);

		assertThat(data).extracting(Data::args).extracting("a").isEqualTo("1");
		assertThat(data).extracting(Data::args).extracting("b").isEqualTo("2");
		assertThat(data).extracting(Data::args).extracting(COMPLEX_STRING).isEqualTo(COMPLEX_STRING);
		assertThat(data).extracting(Data::args).extracting("c").asList().containsExactly("3", "4", "3");
	}

	@Test
	void testGetWithQueryParametersOmitting() {
		var data = webs.get("/get")
			.queryParam("a", null)
			.fetchAs(Data.class);

		assertThat(data.args()).contains(entry("a", ""));

		data = webs.get("/get")
			.omitNullQueryParamValue()
			.queryParam("a", null)
			.fetchAs(Data.class);

		assertThat(data.args()).doesNotContainKeys("a");
	}

	@Test
	void testGetWithQueryString() {
		var data = webs.get("/get")
			.queryParamString("a=1&b=2&c=3&c=4&" + WebsUtil.encodeUrl(COMPLEX_STRING) + "=" + WebsUtil.encodeUrl(COMPLEX_STRING))
			.fetchAs(Data.class);

		assertThat(data).extracting(Data::args).extracting("a").isEqualTo("1");
		assertThat(data).extracting(Data::args).extracting("b").isEqualTo("2");
		assertThat(data).extracting(Data::args).extracting("c").asList().containsExactly("3", "4");
		assertThat(data).extracting(Data::args).extracting(COMPLEX_STRING).isEqualTo(COMPLEX_STRING);
	}

	@Test
	void testGetWithTemplating() {
		var data = webs.get("/get?a={a}&b={b}")
			.bind("a", 1)
			.bind("b", 2)
			.queryParam("c", 3)
			.fetchAs(Data.class);

		assertThat(data).extracting(Data::args)
			.hasFieldOrPropertyWithValue("a", "1")
			.hasFieldOrPropertyWithValue("b", "2")
			.hasFieldOrPropertyWithValue("c", "3");
	}
}
