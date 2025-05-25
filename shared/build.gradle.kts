plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    android()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
            }
        }
        val androidMain by getting
        val desktopMain by getting
    }
}

android {
    namespace = "dev.pandesal.sbp.shared"
    compileSdk = 36
    defaultConfig {
        minSdk = 26
    }
}
