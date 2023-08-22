plugins {
    id("java")
}

group = "net.crizin"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.testcontainers:testcontainers:1.18.3")
}

tasks.test {
    useJUnitPlatform()
}
