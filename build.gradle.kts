plugins {
    id("java")
    id("org.sonarqube") version "4.2.1.3168"
}

group = "net.crizin"
version = "1.0.0"

repositories {
    mavenCentral()
}

sonar {
    properties {
        property("sonar.projectKey", "crizin_webs")
        property("sonar.organization", "crizin")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

dependencies {
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("io.micrometer:micrometer-core:1.11.3")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.testcontainers:testcontainers:1.18.3")
}

tasks.test {
    useJUnitPlatform()
}
