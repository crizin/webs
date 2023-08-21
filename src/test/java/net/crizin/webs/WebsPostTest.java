package net.crizin.webs;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class WebsPostTest extends AbstractTest {

	@Test
	void testPostWithoutPayload() {
		var data = webs.post("/post").fetchAs(Data.class);
		assertThat(data).extracting(Data::form).asInstanceOf(InstanceOfAssertFactories.MAP).isEmpty();
	}

	@Test
	void testPostWithQueryParams() {
		var params = new HashMap<String, String>();
		params.put("a", "1");
		params.put("b", "2");

		var data = webs.post("/post")
			.queryParams(params)
			.queryParam("c", 3)
			.queryParam("c", 3)
			.queryParam(COMPLEX_STRING, COMPLEX_STRING)
			.formValue("f1", 1)
			.formValue("f2", 2)
			.formValue(COMPLEX_STRING, COMPLEX_STRING)
			.fetch()
			.as(Data.class);

		assertThat(data).extracting(Data::args)
			.hasFieldOrPropertyWithValue("a", "1")
			.hasFieldOrPropertyWithValue("b", "2")
			.hasFieldOrPropertyWithValue(COMPLEX_STRING, COMPLEX_STRING)
			.extracting("c").asList().containsExactly("3", "3");

		assertThat(data).extracting(Data::form)
			.hasFieldOrPropertyWithValue("f1", "1")
			.hasFieldOrPropertyWithValue("f2", "2")
			.hasFieldOrPropertyWithValue(COMPLEX_STRING, COMPLEX_STRING);
	}

	@Test
	void testPostWithCustomPayload() {
		var payload = new ParamsBuilder()
			.omitNullValue()
			.add("f1", 1)
			.add("f2", 2)
			.add(COMPLEX_STRING, COMPLEX_STRING)
			.buildAsString();

		var data = webs.post("/post")
			.payload(payload)
			.fetchAs(Data.class);

		assertThat(data).extracting(Data::headers)
			.hasFieldOrPropertyWithValue("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

		assertThat(data).extracting("form")
			.hasFieldOrPropertyWithValue("f1", "1")
			.hasFieldOrPropertyWithValue("f2", "2")
			.hasFieldOrPropertyWithValue(COMPLEX_STRING, COMPLEX_STRING);

		data = webs.post("/post")
			.jsonPayload(Collections.singletonMap("a", 1))
			.fetch()
			.as(Data.class);

		assertThat(data).extracting(Data::headers)
			.hasFieldOrPropertyWithValue("Content-Type", "application/json; charset=UTF-8");

		assertThat(data).extracting(Data::form)
			.asInstanceOf(InstanceOfAssertFactories.MAP)
			.isEmpty();

		assertThat(data).extracting(Data::data).asString()
			.startsWith("{")
			.endsWith("}");
	}

	@Test
	void testPostWithOmitNullFormValue() {
		var data = webs.post("/post")
			.formValue("a", 1)
			.formValue("b", null)
			.fetchAs(Data.class);

		assertThat(data).extracting(Data::form)
			.hasFieldOrPropertyWithValue("a", "1")
			.hasFieldOrPropertyWithValue("b", "");

		data = webs.post("/post")
			.omitNullValue()
			.formValue("a", 1)
			.formValue("b", null)
			.fetchAs(Data.class);

		assertThat(data).extracting(Data::form)
			.asInstanceOf(InstanceOfAssertFactories.MAP)
			.hasFieldOrPropertyWithValue("a", "1")
			.doesNotContainKey("b");
	}
}
