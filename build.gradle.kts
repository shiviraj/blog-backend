import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"
    idea
    jacoco
}

group = "com.blog"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.security:spring-security-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("bouncycastle:bcprov-jdk16:136")
    implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("org.aspectj:aspectjweaver:1.9.6")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools:2.5.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.3")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.3.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.3.2")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:3.0.0")
    testImplementation("io.projectreactor:reactor-test:3.4.8")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.6.0")
    testImplementation("com.squareup.okhttp3:okhttp:4.6.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets.create("integrationTest") {
    withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
        kotlin.srcDir("src/integrationTest/kotlin")
        compileClasspath += sourceSets.main.get().output + configurations.testCompileClasspath
        runtimeClasspath += output + compileClasspath + configurations.testRuntimeClasspath
    }
    resources.srcDir("src/integrationTest/resources")
}


tasks.register<Test>("integrationTest") {
    useJUnitPlatform()
    testClassesDirs = sourceSets.getByName("integrationTest").output.classesDirs
    classpath = sourceSets.getByName("integrationTest").runtimeClasspath
}

tasks.register<Delete>("cleanIntegrationTest") {
    delete.add("build/classes/kotlin/integrationTest")
    delete.add("build/kotlin/compileIntegrationTestKotlin")
    delete.add("build/reports/tests/integrationTest")
    delete.add("build/resources/integrationTest")
    delete.add("build/test-results/integrationTest")
}

idea {
    module {
        testSourceDirs = sourceSets.getByName("integrationTest").allSource.srcDirs
    }
}

// Jacoco configuration

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    finalizedBy(tasks.jacocoTestCoverageVerification) // coverage verification is always performed after tests run
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport) // tests are required to run before generating the report
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = BigDecimal("0.41")
            }
        }

        rule {
            limit {
                counter = "BRANCH"
                minimum = "0.50".toBigDecimal()
            }
        }
    }
}
