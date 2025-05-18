plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id(libs.plugins.kotlin.serialization.get().pluginId)
}

android {
    namespace = "dev.pandesal.sbp"
    compileSdk = 36

    val versionMajor: String by project
    val versionMinor: String by project
    val versionPatch: String by project
    val computedVersionCode = versionMajor.toInt() * 10000 + versionMinor.toInt() * 100 + versionPatch.toInt()

    defaultConfig {
        applicationId = "dev.pandesal.sbp"
        minSdk = 26
        targetSdk = 36
        versionCode = computedVersionCode
        versionName = "$versionMajor.$versionMinor.$versionPatch"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.core.android)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.accompanist.pager)

    implementation(libs.androidx.room.common)

    implementation(libs.hilt)
    ksp(libs.hilt.compiler)

    implementation(libs.hilt.testing)
    kspAndroidTest(libs.hilt.testing.compiler)

    //Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)
    implementation(libs.reorderable)

    implementation(libs.generativeai)
    implementation(libs.kotlinx.serialization.json)


    testImplementation(libs.junit)
    testImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)

}