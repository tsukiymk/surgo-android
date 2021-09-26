plugins {
    id("com.gradle.enterprise").version("3.0")
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

rootProject.name = "Surgo"

include(":shared")
include(":surgo-openapi")
include(":data")
include(":core")
include(":domain")
include(":common-resources")
include(":common-compose")
include(":ui-albumdetails")
include(":ui-artistdetails")
include(":ui-explore")
include(":ui-feed")
include(":ui-library")
include(":ui-playback")
include(":ui-playlistdetails")
include(":ui-plugindetails")
include(":ui-settings")
include(":app")

// Enable Gradle's version catalog support
// https://docs.gradle.org/current/userguide/platforms.html
enableFeaturePreview("VERSION_CATALOGS")
