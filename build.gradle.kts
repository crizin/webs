plugins {
    id("java")
}

group = "net.crizin"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.httpcomponents.client5:httpclient5:5.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0")
    implementation("io.micrometer:micrometer-core:1.15.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("org.testcontainers:testcontainers:1.21.0")
}

tasks.test {
    useJUnitPlatform()
}
