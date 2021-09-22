import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("kotlin")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

java.sourceCompatibility = JavaVersion.VERSION_1_8

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(kotlin("stdlib"))
}

tasks {
    dokkaHtml.configure {
        outputDirectory.set(buildDir.resolve("dokka"))
    }

    register("sourcesJar", Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.getByName("main").java.srcDirs)
    }

    register("javadocJar", Jar::class) {
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
                val javadocJar by tasks

                artifact(sourcesJar)
                artifact(javadocJar)
                from(components["java"])

                groupId = "com.tsukiymk.surgo"
                artifactId = "surgo-openapi"
                version = "1.0.0"
            }
        }
    }
}
