plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.google.devtools.ksp")
}

android {
    compileSdk = ApplicationConfig.COMPILE_SDK_VERSION
    defaultConfig {
        minSdk = ApplicationConfig.MIN_SDK_VERSION
        targetSdk = ApplicationConfig.TARGET_SDK_VERSION
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.incremental"] = "true"
            }
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":shared"))
    implementation(project(":surgo-openapi"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(kotlin("stdlib"))

    // Jetpack
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.media2.session)
    implementation(libs.androidx.room.room)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.work.runtime)
    ksp(libs.androidx.room.compiler)

    // Dagger
    implementation(libs.dagger.hiltAndroid)
    kapt(libs.dagger.hiltAndroidCompiler)

    // ExoPlayer
    implementation(libs.android.exoplayer.core)
    implementation(libs.android.exoplayer.ui)
    implementation(libs.android.exoplayer.extensionMediaSession)
    implementation(libs.android.exoplayer.extensionCast)

    // Desugar
    coreLibraryDesugaring(libs.android.tools.desugarJdkLibs)

    // Local unit tests
    testImplementation(libs.junit.jUnit)

    // Instrumentation tests
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.ext.jUnit)
}
