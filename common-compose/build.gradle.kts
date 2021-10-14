plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = ApplicationConfig.COMPILE_SDK_VERSION
    defaultConfig {
        minSdk = ApplicationConfig.MIN_SDK_VERSION
        targetSdk = ApplicationConfig.TARGET_SDK_VERSION
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.get()
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(kotlin("stdlib"))

    // Jetpack
    implementation(libs.androidx.compose.material.material)
    implementation(libs.androidx.compose.runtime.runtime)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.core.core)
    implementation(libs.androidx.palette.palette)

    // Dagger
    implementation(libs.dagger.hiltAndroid)
    kapt(libs.dagger.hiltAndroidCompiler)

    // Accompanist
    implementation(libs.accompanist.insets)

    // Coil
    implementation(libs.coilKt.compose)

    // Local unit tests
    testImplementation(libs.junit.jUnit)

    // Instrumentation tests
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.ext.jUnit)
}
