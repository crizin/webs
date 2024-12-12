plugins {
    id("java")
    id("jacoco")
}

group = "net.crizin"
version = "1.0.0"

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.12"
}

dependencies {
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2")
    implementation("io.micrometer:micrometer-core:1.14.2")
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("org.testcontainers:testcontainers:1.20.4")
}

tasks.test {
    useJUnitPlatform()
}
