plugins {
    id("java")
}

group = "net.crizin"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.httpcomponents.client5:httpclient5:5.5.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.20.0")
    implementation("io.micrometer:micrometer-core:1.15.4")
	testImplementation("org.junit.platform:junit-platform-launcher:6.0.0")
	testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
	testImplementation("org.assertj:assertj-core:3.27.6")
    testImplementation("org.testcontainers:testcontainers:1.21.3")
}

tasks.test {
    useJUnitPlatform()
}
