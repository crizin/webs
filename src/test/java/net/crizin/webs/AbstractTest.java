package net.crizin.webs;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

abstract class AbstractTest {

	protected Webs webs;
	protected final String complexString = "<!--'\"&lt;\\n+#]]>흫흣</script>";

	@BeforeEach
	void before() {
		webs = Webs.builder()
				.baseUrl("https://httpbin.org")
				.setConnectionTimeout(Duration.ofSeconds(1))
				.setReadTimeout(Duration.ofSeconds(30))
				.build();
	}

	@AfterEach
	void close() {
		webs.close();
	}
}