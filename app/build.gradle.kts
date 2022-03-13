import dependency.Dependencies
import config.ConfigData

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    defaultConfig {
        applicationId = "aueb.gr.nasiakouts.popularmovies"
        compileSdk = ConfigData.COMPILE_SDK_VERSION
        minSdk = ConfigData.MIN_SDK_VERSION
        targetSdk = ConfigData.TARGET_SDK_VERSION
        versionCode = ConfigData.APP_VERSION_CODE
        versionName = ConfigData.APP_VERSION_NAME
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(Dependencies.AndroidX.APP_COMPAT)
    implementation(Dependencies.AndroidX.CONSTRAINT_LAYOUT)
    implementation(Dependencies.AndroidX.LEGACY_SUPPORT)
    implementation(Dependencies.AndroidX.CARDVIEW)
    implementation(Dependencies.AndroidX.RECYCLERVIEW)
    implementation(Dependencies.Misc.PICASSO)
    implementation(Dependencies.Misc.GSON_CONVERTER)
    implementation(Dependencies.Misc.MATERIAL)
    implementation(Dependencies.Misc.CAROUSELVIEW)
}
