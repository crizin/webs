package net.crizin.webs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;

import java.time.Duration;

abstract class AbstractTest {

	protected static final String COMPLEX_STRING = "<!--'\"&lt;\\n+#]]>흫흣</script>";
	protected static GenericContainer<?> httpBin;
	protected static Webs webs;

	@BeforeAll
	@SuppressWarnings("resource")
	public static void before() {
		httpBin = new GenericContainer<>("kennethreitz/httpbin")
			.withExposedPorts(80)
			.withReuse(true);
		httpBin.start();

		webs = Webs.builder()
			.baseUrl(getBaseUrl())
			.acceptCodes(200, 401)
			.setConnectionTimeout(Duration.ofSeconds(1))
			.setReadTimeout(Duration.ofMinutes(1))
			.build();
	}

	@AfterAll
	public static void after() {
		webs.close();
		httpBin.close();
	}

	protected static String getBaseUrl() {
		return "http://%s:%d".formatted(httpBin.getHost(), httpBin.getMappedPort(80));
	}
}
