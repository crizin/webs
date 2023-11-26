plugins {
    id("java")
    id("jacoco")
    id("org.sonarqube") version "4.2.1.3168"
}

group = "net.crizin"
version = "1.0.0"

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.10"
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
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
    implementation("io.micrometer:micrometer-core:1.12.0")
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.testcontainers:testcontainers:1.19.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
}
