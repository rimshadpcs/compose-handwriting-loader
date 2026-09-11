import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    android {
        namespace = "com.rimshadpcs.composehandwriting"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // iosX64 (Intel simulator) deliberately omitted — matches what this Compose Multiplatform
    // version actually publishes artifacts for (arm64 device + arm64 simulator only); requesting
    // it fails dependency resolution for compose.runtime/foundation/ui with an "Unresolved
    // platforms: [iosX64]" error.
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeHandwritingLoader"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            // Only for LocalContentColor (the default `color` follows the caller's Material
            // theme) and Text. A reasonable dependency for a Compose Multiplatform UI library —
            // most consumers will already be on Material3 — but if that ever needs to change,
            // this is the one place it's pulled in.
            implementation(compose.material3)
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.compose.uiTooling)
            }
        }
    }
}

// Publishes to Maven Central via the vanniktech plugin, reading coordinates/POM metadata from
// gradle.properties (GROUP/VERSION_NAME/POM_*) at the root. Signing/credentials come from
// ~/.gradle/gradle.properties or environment variables at publish time — never from this file —
// so this build script alone is safe to keep public with no secrets in it.
//
// No explicit coordinates(...) call here: the Android KMP library plugin already finalizes
// groupId itself, and calling coordinates() afterward conflicts with that ("property is final").
// groupId/version come from gradle.properties' GROUP/VERSION_NAME (vanniktech's own convention),
// and artifactId defaults to this module's own name — which is why this directory is named
// compose-handwriting-loader rather than a generic "library".
mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
