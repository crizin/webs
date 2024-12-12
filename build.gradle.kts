plugins {
	id("java")
	id("jacoco")
	id("org.sonarqube") version "6.0.1.5171"
}

group = "net.crizin"
version = "1.0.0"

repositories {
	mavenCentral()
}

jacoco {
	toolVersion = "0.8.12"
}

sonar {
	properties {
		property("sonar.projectKey", "crizin_webs")
		property("sonar.organization", "crizin")
		property("sonar.host.url", "https://sonarcloud.io")
	}
}

val httpClientVersion = "5.4.1"
val jacksonVersion = "2.18.2"
val micrometerVersion = "1.14.2"
val junitVersion = "5.11.3"
val assertjVersion = "3.26.3"
val testcontainersVersion = "1.20.4"

dependencies {
	implementation("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
	implementation("io.micrometer:micrometer-core:$micrometerVersion")
	testImplementation(platform("org.junit:junit-bom:$junitVersion"))
	testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
	testImplementation("org.assertj:assertj-core:$assertjVersion")
	testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
}

tasks.test {
	useJUnitPlatform()
}

tasks.jacocoTestReport {
	reports {
		xml.required = true
	}
}
