plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("checkstyle")
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.m"
version = "0.0.2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

spotless {
    java {
        indentWithTabs(4)
        indentWithSpaces(4)
        endWithNewline()
        trimTrailingWhitespace()
        removeUnusedImports()
    }
}

checkstyle {
    toolVersion = "10.15.0"
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.postgresql:postgresql:42.7.3")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
    testImplementation("org.mockito:mockito-core")

}


tasks.withType<Checkstyle>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(false)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
