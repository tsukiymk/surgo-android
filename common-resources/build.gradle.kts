plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = ApplicationConfig.COMPILE_SDK_VERSION
    defaultConfig {
        minSdk = ApplicationConfig.MIN_SDK_VERSION
        targetSdk = ApplicationConfig.TARGET_SDK_VERSION
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // UI
    implementation(libs.android.material.material)
}
