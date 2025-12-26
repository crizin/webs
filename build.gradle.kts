plugins {
    id("java")
}

group = "net.crizin"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.httpcomponents.client5:httpclient5:5.6")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.20.1")
    implementation("io.micrometer:micrometer-core:1.16.1")
	testImplementation("org.junit.platform:junit-platform-launcher:6.0.1")
	testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
	testImplementation("org.assertj:assertj-core:3.27.6")
    testImplementation("org.testcontainers:testcontainers:2.0.3")
}

tasks.test {
    useJUnitPlatform()
}
