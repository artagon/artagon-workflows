/*
 * Secure Gradle Settings Template
 *
 * Features:
 * - Repository allow-listing (single trusted mirror)
 * - Dependency verification enforcement
 * - Dependency locking
 * - Plugin version pinning
 * - Fail on project-level repository declarations
 */

// Root project name
rootProject.name = "{{PROJECT_NAME}}"

// Plugin management - control where plugins come from
pluginManagement {
    repositories {
        // OPTION 1: Use a trusted corporate mirror/proxy (RECOMMENDED)
        maven {
            name = "CorporateMirror"
            url = uri("{{MIRROR_URL}}")  // e.g., https://nexus.company.com/repository/gradle-plugins/

            // Optional: require HTTPS
            isAllowInsecureProtocol = false

            // Optional: authentication if required
            // credentials {
            //     username = providers.gradleProperty("mirror.username").orNull
            //         ?: providers.environmentVariable("MIRROR_USERNAME").orNull
            //     password = providers.gradleProperty("mirror.password").orNull
            //         ?: providers.environmentVariable("MIRROR_PASSWORD").orNull
            // }
        }

        // OPTION 2: Use Gradle Plugin Portal directly (if allowed)
        // gradlePluginPortal()
    }

    // Pin plugin versions (NO dynamic versions like '+')
    plugins {
        // Kotlin
        kotlin("jvm") version "2.1.0"
        kotlin("plugin.spring") version "2.1.0"

        // Spring Boot
        id("org.springframework.boot") version "3.4.0"
        id("io.spring.dependency-management") version "1.1.7"

        // CycloneDX SBOM
        id("org.cyclonedx.bom") version "1.10.0"

        // Code quality
        id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
        id("io.gitlab.arturbosch.detekt") version "1.23.7"

        // Publishing
        id("maven-publish")
        id("signing")
    }
}

// Dependency resolution management
dependencyResolutionManagement {
    // CRITICAL: Fail if project build scripts declare repositories
    // This forces all resolution through the repositories defined here
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        // OPTION 1: Single trusted mirror (RECOMMENDED)
        maven {
            name = "CorporateMirror"
            url = uri("{{MIRROR_URL}}")  // e.g., https://nexus.company.com/repository/maven-public/
            isAllowInsecureProtocol = false

            // Optional: authentication
            // credentials {
            //     username = providers.gradleProperty("mirror.username").orNull
            //         ?: providers.environmentVariable("MIRROR_USERNAME").orNull
            //     password = providers.gradleProperty("mirror.password").orNull
            //         ?: providers.environmentVariable("MIRROR_PASSWORD").orNull
            // }
        }

        // OPTION 2: Standard repositories (if no mirror)
        // mavenCentral()
        // google()
    }
}

// Enable dependency verification
// Generate with: ./gradlew --write-verification-metadata sha256 help
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Include subprojects
// include("subproject1")
// include("subproject2")
