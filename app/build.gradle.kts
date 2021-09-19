plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = ApplicationConfig.COMPILE_SDK_VERSION
    signingConfigs {
        fun readProperty(propertyName: String, defaultValue: String): String = project.properties[propertyName]?.toString() ?: defaultValue

        create("release") {
            storeFile = rootProject.file("release.jks")
            storePassword = readProperty("SIGNING_RELEASE_KEYSTORE_PWD", "")
            keyAlias = readProperty("SIGNING_RELEASE_KEY_ALIAS", "")
            keyPassword = readProperty("SIGNING_RELEASE_KEY_PWD", "")
        }
    }
    defaultConfig {
        applicationId = "com.tsukiymk.surgo"
        minSdk = ApplicationConfig.MIN_SDK_VERSION
        targetSdk = ApplicationConfig.TARGET_SDK_VERSION
        versionCode = 1
        versionName = "1.0.0-Canary"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            signingConfig = signingConfigs.getByName("release")
        }
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
    implementation(project(":common-compose"))
    implementation(project(":common-resources"))
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":shared"))
    implementation(project(":surgo-openapi"))
    implementation(project(":ui-albumdetails"))
    implementation(project(":ui-artistdetails"))
    implementation(project(":ui-explore"))
    implementation(project(":ui-feed"))
    implementation(project(":ui-library"))
    implementation(project(":ui-playback"))
    implementation(project(":ui-playlistdetails"))
    implementation(project(":ui-settings"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    // Jetpack
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material.material)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.runtime.runtime)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.core.core)
    implementation(libs.androidx.hilt.lifecycleViewModel)
    implementation(libs.androidx.hilt.navigationCompose)
    implementation(libs.androidx.media2.session)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.hilt.compiler)

    // UI
    implementation(libs.android.material.material)

    // Dagger
    implementation(libs.dagger.hiltAndroid)
    kapt(libs.dagger.hiltAndroidCompiler)

    // Accompanist
    implementation(libs.accompanist.insets)
    implementation(libs.accompanist.systemUiController)

    // ExoPlayer
    implementation(libs.android.exoplayer.core)
    implementation(libs.android.exoplayer.ui)
    implementation(libs.android.exoplayer.extensionMediaSession)
    implementation(libs.android.exoplayer.extensionCast)

    // Coil
    implementation(libs.coil.compose)

    // Local unit tests
    testImplementation(libs.junit.jUnit)

    // Instrumentation tests
    androidTestImplementation(libs.androidx.compose.ui.testJUnit4)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.ext.jUnit)
}
