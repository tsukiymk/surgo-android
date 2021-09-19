import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.tools.build.gradle)
        classpath(kotlin("gradle-plugin", libs.versions.kotlin.get()))
        classpath(libs.dagger.hiltAndroidGradle)
        classpath(libs.jetbrains.dokka.gradle)
    }
}

plugins {
    id("com.github.ben-manes.versions").version("0.39.0")
}

subprojects {
    repositories {
        google()
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()

            freeCompilerArgs = freeCompilerArgs.plus("-Xopt-in=kotlin.RequiresOptIn")
            freeCompilerArgs = freeCompilerArgs.plus("-Xopt-in=kotlin.Experimental")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        val currentRelease = DependencyUpdates.versionToRelease(currentVersion)
        if (currentRelease == ReleaseType.SNAPSHOT) return@rejectVersionIf true

        DependencyUpdates.versionToRelease(candidate.version).isLessStableThan(currentRelease)
    }
}
