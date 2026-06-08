import com.google.protobuf.gradle.*

plugins {
	java
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.0"
	id("com.google.protobuf") version "0.9.4"
	id("jacoco")
}

group = "ru.bauman.seminar"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Database
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.liquibase:liquibase-core")

	// Protobuf (для десериализации Kafka-сообщений)
	implementation("io.grpc:grpc-protobuf:1.62.2")
	compileOnly("org.apache.tomcat:annotations-api:6.0.53")

	// Kafka
	implementation("org.springframework.kafka:spring-kafka")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Swagger/OpenAPI
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

	// MapStruct
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:testcontainers:1.19.3")
	testImplementation("org.testcontainers:postgresql:1.19.3")
	testImplementation("org.testcontainers:junit-jupiter:1.19.3")
	testImplementation("com.h2database:h2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:3.25.3"
	}
}

sourceSets {
	main {
		java {
			srcDirs("build/generated/source/proto/main/java")
		}
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("file.encoding", "UTF-8")
}

tasks.withType<JavaExec> {
	systemProperty("file.encoding", "UTF-8")
}

tasks.bootRun {
	systemProperty("spring.profiles.active", "local")
	systemProperty("file.encoding", "UTF-8")
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(false)
		csv.required.set(false)
		html.required.set(true)
	}
}
