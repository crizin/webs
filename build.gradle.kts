plugins {
    id("java")
    id("jacoco")
    id("org.sonarqube") version "4.4.1.3373"
}

group = "net.crizin"
version = "1.0.0"

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.11"
}

sonar {
    properties {
        property("sonar.projectKey", "crizin_webs")
        property("sonar.organization", "crizin")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

dependencies {
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")
    implementation("io.micrometer:micrometer-core:1.12.2")
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.testcontainers:testcontainers:1.19.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
}
