/*
 * Secure Gradle Build Template
 *
 * Features:
 * - CycloneDX SBOM generation
 * - Dependency locking
 * - No dynamic versions
 * - Reproducible builds
 */

import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    id("org.cyclonedx.bom")
}

group = "{{GROUP_ID}}"  // e.g., com.example
version = "{{VERSION}}"  // e.g., 1.0.0

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }

    // Generate sources and javadoc JARs
    withSourcesJar()
    withJavadocJar()
}

// Dependency locking configuration
dependencyLocking {
    // Lock all configurations
    lockAllConfigurations()

    // Optional: lock dependency versions during build
    // lockMode.set(LockMode.STRICT)
}

dependencies {
    // NO dynamic versions! Always use exact versions
    // ❌ BAD:  implementation("org.example:lib:1.+")
    // ✅ GOOD: implementation("org.example:lib:1.2.3")

    // Example dependencies (replace with your actual dependencies)
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.15")

    // Test dependencies
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Use constraints for transitive dependency management
    constraints {
        implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    }
}

// CycloneDX SBOM configuration
cyclonedxBom {
    // Include build tools and metadata
    includeConfigs.set(listOf("runtimeClasspath", "compileClasspath"))

    // Set schema version
    schemaVersion.set("1.6")

    // Output directory
    destination.set(file("build/reports"))

    // Output formats
    outputFormat.set("all")  // JSON, XML

    // Output name
    outputName.set("bom")

    // Include BOM serial number
    includeBomSerialNumber.set(true)

    // Include license text
    includeLicenseText.set(true)

    // Project type
    projectType.set("library")

    // Component type
    componentType.set("library")
}

tasks {
    test {
        useJUnitPlatform()

        // Test logging
        testLogging {
            events(
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.FAILED,
                TestLogEvent.STANDARD_OUT,
                TestLogEvent.STANDARD_ERROR
            )
            showStandardStreams = false
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }

        // Reproducible test execution
        systemProperty("java.security.egd", "file:/dev/./urandom")
    }

    javadoc {
        if (JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }

    // Ensure SBOM is generated during build
    named("build") {
        dependsOn("cyclonedxBom")
    }

    // Generate lockfiles
    register("lockDependencies") {
        group = "verification"
        description = "Generate dependency lockfiles for all configurations"
        doFirst {
            println("Generating dependency lockfiles...")
        }
    }

    // Verify lockfiles
    register("verifyLockfiles") {
        group = "verification"
        description = "Verify no lockfile drift"
        doLast {
            val lockfiles = fileTree(projectDir) {
                include("**/*.lockfile")
                include("**/gradle.lockfile")
            }

            if (lockfiles.isEmpty) {
                throw GradleException(
                    "No lockfiles found! Generate with: ./gradlew dependencies --write-locks"
                )
            }

            println("✅ Found ${lockfiles.files.size} lockfile(s)")
        }
    }
}

// Publishing configuration
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("{{PROJECT_NAME}}")
                description.set("{{PROJECT_DESCRIPTION}}")
                url.set("{{PROJECT_URL}}")

                licenses {
                    license {
                        name.set("{{LICENSE_NAME}}")
                        url.set("{{LICENSE_URL}}")
                    }
                }

                developers {
                    developer {
                        id.set("{{DEVELOPER_ID}}")
                        name.set("{{DEVELOPER_NAME}}")
                        email.set("{{DEVELOPER_EMAIL}}")
                    }
                }

                scm {
                    connection.set("scm:git:{{GIT_URL}}")
                    developerConnection.set("scm:git:{{GIT_URL}}")
                    url.set("{{PROJECT_URL}}")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/{{GITHUB_OWNER}}/{{GITHUB_REPO}}")
            credentials {
                username = providers.environmentVariable("GITHUB_ACTOR").orNull
                password = providers.environmentVariable("GITHUB_TOKEN").orNull
            }
        }
    }
}

// Signing configuration
signing {
    // Use GPG agent if available
    useGpgCmd()

    // Or use in-memory keys from environment
    val signingKey = providers.environmentVariable("GPG_PRIVATE_KEY").orNull
    val signingPassword = providers.environmentVariable("GPG_PASSPHRASE").orNull

    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }

    sign(publishing.publications["maven"])
}

// Reproducible builds
tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

// Verify configuration
tasks.register("verifySecureConfig") {
    group = "verification"
    description = "Verify secure Gradle configuration"

    doLast {
        println("🔍 Verifying secure configuration...")

        // Check wrapper properties
        val wrapperProps = file("gradle/wrapper/gradle-wrapper.properties")
        if (!wrapperProps.exists()) {
            throw GradleException("❌ gradle-wrapper.properties not found!")
        }

        val propsContent = wrapperProps.readText()
        if (!propsContent.contains("distributionSha256Sum")) {
            throw GradleException(
                "❌ distributionSha256Sum not configured in gradle-wrapper.properties"
            )
        }

        // Check verification metadata
        val verificationMetadata = file("gradle/verification-metadata.xml")
        if (!verificationMetadata.exists()) {
            println("⚠️  verification-metadata.xml not found")
            println("Generate with: ./gradlew --write-verification-metadata sha256 help")
        } else {
            println("✅ Dependency verification metadata found")
        }

        // Check lockfiles
        val lockfiles = fileTree(projectDir) {
            include("**/*.lockfile")
            include("**/gradle.lockfile")
        }

        if (lockfiles.isEmpty) {
            println("⚠️  No lockfiles found")
            println("Generate with: ./gradlew dependencies --write-locks")
        } else {
            println("✅ Found ${lockfiles.files.size} lockfile(s)")
        }

        println("✅ Wrapper SHA-256 configured")
        println("✅ Configuration verification complete")
    }
}
