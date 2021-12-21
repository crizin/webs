package com.github.crizin.webs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class WebsPostTest extends AbstractTest {

	@Test
	void testPostWithoutPayload() {
		Data data = webs.post("/post").fetch().as(Data.class);
		assertThat(data).extracting(Data::getForm).asInstanceOf(InstanceOfAssertFactories.MAP).isEmpty();
	}

	@Test
	void testPostWithQueryParams() {
		Map<String, String> params = new HashMap<>();
		params.put("a", "1");
		params.put("b", "2");

		Data data = webs.post("/post")
				.queryParams(params)
				.queryParam("c", 3)
				.queryParam("c", 3)
				.queryParam(complexString, complexString)
				.formValue("f1", 1)
				.formValue("f2", 2)
				.formValue(complexString, complexString)
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getArgs)
				.hasFieldOrPropertyWithValue("a", "1")
				.hasFieldOrPropertyWithValue("b", "2")
				.hasFieldOrPropertyWithValue(complexString, complexString)
				.extracting("c").asList().containsExactly("3", "3");

		assertThat(data).extracting(Data::getForm)
				.hasFieldOrPropertyWithValue("f1", "1")
				.hasFieldOrPropertyWithValue("f2", "2")
				.hasFieldOrPropertyWithValue(complexString, complexString);
	}

	@Test
	void testPostWithCustomPayload() {
		String payload = new ParamsBuilder()
				.omitNullValue()
				.add("f1", 1)
				.add("f2", 2)
				.add(complexString, complexString)
				.buildAsString();

		Data data = webs.post("/post")
				.payload(payload)
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getHeaders)
				.hasFieldOrPropertyWithValue("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

		assertThat(data).extracting("form")
				.hasFieldOrPropertyWithValue("f1", "1")
				.hasFieldOrPropertyWithValue("f2", "2")
				.hasFieldOrPropertyWithValue(complexString, complexString);

		data = webs.post("/post")
				.jsonPayload(data)
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getHeaders)
				.hasFieldOrPropertyWithValue("Content-Type", "application/json; charset=UTF-8");

		assertThat(data).extracting(Data::getForm)
				.asInstanceOf(InstanceOfAssertFactories.MAP)
				.isEmpty();

		assertThat(data).extracting(Data::getData).asString()
				.startsWith("{")
				.endsWith("}");
	}

	@Test
	void testPostWihtOmitNullFormValue() {
		Data data = webs.post("/post")
				.formValue("a", 1)
				.formValue("b", null)
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getForm)
				.hasFieldOrPropertyWithValue("a", "1")
				.hasFieldOrPropertyWithValue("b", "");

		data = webs.post("/post")
				.omitNullValue()
				.formValue("a", 1)
				.formValue("b", null)
				.fetch()
				.as(Data.class);

		assertThat(data).extracting(Data::getForm)
				.asInstanceOf(InstanceOfAssertFactories.MAP)
				.hasFieldOrPropertyWithValue("a", "1")
				.doesNotContainKey("b");
	}
}