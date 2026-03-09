plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "investiment.message"
version = "0.0.1-SNAPSHOT"
description = "API for processing investiments data"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Import the Spring Cloud BOM to manage dependency versions
	implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2024.0.3"))

	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.apache.kafka:kafka-streams")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.kafka:spring-kafka")
	// The dependency declaration for OpenFeign remains the same
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

	// JSON Processing
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
	testImplementation("org.mockito:mockito-junit-jupiter:5.6.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootBuildImage {
	// Note: 'runImage' property might need adjustment based on specific requirements,
	// but the original line is kept as it's not related to the "Unresolved reference" error.
	runImage = "paketobuildpacks/ubuntu-noble-run:latest"
}