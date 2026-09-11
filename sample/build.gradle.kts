plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.rimshadpcs.composehandwriting.sample"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.rimshadpcs.composehandwriting.sample"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":compose-handwriting-loader"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    // compose.* (not libs.compose.*) — the Compose Multiplatform Gradle plugin's own accessors,
    // guaranteed to resolve to versions that actually exist and work together, unlike hardcoding
    // a version-catalog entry per artifact (material3 in particular ships under its own version
    // number, not composeMultiplatform's).
    implementation(compose.ui)
    implementation(compose.material3)
    implementation(compose.preview)
    debugImplementation(compose.uiTooling)
}
