import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

android {
    compileSdk = ApplicationConfig.COMPILE_SDK_VERSION
    defaultConfig {
        minSdk = ApplicationConfig.MIN_SDK_VERSION
        targetSdk = ApplicationConfig.TARGET_SDK_VERSION
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(kotlin("stdlib"))
}

tasks {
    dokkaHtml.configure {
        outputDirectory.set(buildDir.resolve("dokka"))
    }

    withType<DokkaTask>().configureEach {
        dokkaSourceSets {
            named("main") {
                noAndroidSdkLink.set(true)
            }
        }
    }

    register("sourcesJar", Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }

    register("androidJavadocJar", Jar::class) {
        val tasks = project.tasks.withType(DokkaTask::class.java)
        val dokkaTask = tasks.findByName("dokkaHtml") ?: tasks.getByName("dokka")

        archiveClassifier.set("javadoc")
        dependsOn(dokkaTask)
        from(dokkaTask)
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                val sourcesJar by tasks
                val androidJavadocJar by tasks

                artifact(sourcesJar)
                artifact(androidJavadocJar)
                from(components["release"])

                groupId = "com.tsukiymk.surgo"
                artifactId = "surgo-openapi"
                version = "1.0.0"
            }
        }
    }
}
