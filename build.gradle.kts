import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.0.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.3.50"
	kotlin("plugin.spring") version "1.3.50"
	kotlin("plugin.jpa") version "1.3.50"
}

group = "com.mitchmele"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
	runtimeClasspath {
		extendsFrom(developmentOnly)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springCloudVersion"] = "Hoxton.M3"

dependencies {
	compile ("org.springframework.cloud:spring-cloud-stream")
	compile ("org.springframework.cloud:spring-cloud-starter-stream-rabbit")
	compile ("org.springframework.cloud:spring-cloud-stream-binder-kafka")
	compile ("org.jetbrains.kotlin:kotlin-test")
	compile("com.fasterxml.jackson.module:jackson-module-kotlin")
	compile ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")

	compile ("org.apache.logging.log4j:log4j-core:2.10.0")
	compile ("org.apache.logging.log4j:log4j-api:2.10.0")

	testCompile ("com.willowtreeapps.assertk:assertk-jvm:0.20")
	testCompile("com.nhaarman:mockito-kotlin:1.5.0")
	testCompile("org.assertj:assertj-core:3.11.1")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-integration")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.cloud:spring-cloud-starter")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.springframework.integration:spring-integration-test")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.test {
	useJUnitPlatform()
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
