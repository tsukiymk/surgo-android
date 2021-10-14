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
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":surgo-openapi"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(kotlin("stdlib"))

    // Jetpack
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.compose.runtime.runtime)
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.room.runtime)

    // Dagger
    implementation(libs.dagger.hiltAndroid)
    kapt(libs.dagger.hiltAndroidCompiler)

    // Store
    implementation(libs.store.store4)

    // Desugar
    coreLibraryDesugaring(libs.android.tools.desugarJdkLibs)

    // Local unit tests
    testImplementation(libs.junit.jUnit)

    // Instrumentation tests
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.ext.jUnit)
}
