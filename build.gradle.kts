import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.5.RELEASE"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "1.4.20"
	kotlin("plugin.spring") version "1.4.20"
}

group = "com.github.nayasis"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenLocal()
	mavenCentral()
	jcenter()
	maven { url = uri("https://jitpack.io") }
}

configurations.all {
	resolutionStrategy.cacheChangingModulesFor(  0, "seconds" )
	resolutionStrategy.cacheDynamicVersionsFor(  5, "minutes" )
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web") {
		exclude(group="org.springframework.boot",module="spring-boot-starter-tomcat")
	}
	implementation("org.springframework.boot:spring-boot-starter-undertow")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation( "io.github.microutils:kotlin-logging:1.8.3" )
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// for lock registry core
	implementation("org.springframework.boot:spring-boot-starter-integration")

	// for zookeeper lock registry
	implementation("org.springframework.integration:spring-integration-zookeeper:5.4.4")

	// for redis lock registry
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.integration:spring-integration-redis:5.4.4")

	// for jdbc lock registry
	implementation("org.springframework.integration:spring-integration-jdbc:5.4.4")
	implementation("org.springframework.integration:spring-integration-core:5.4.4")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("com.h2database:h2")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
