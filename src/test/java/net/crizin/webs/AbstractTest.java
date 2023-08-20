package net.crizin.webs;

import java.time.Duration;

abstract class AbstractTest {

	protected static final String COMPLEX_STRING = "<!--'\"&lt;\\n+#]]>흫흣</script>";

	protected final Webs webs = Webs.builder()
		.baseUrl("https://httpbin.org")
		.acceptCodes(200, 401)
		.setConnectionTimeout(Duration.ofSeconds(1))
		.setReadTimeout(Duration.ofMinutes(1))
		.build();
}
