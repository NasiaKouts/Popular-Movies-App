plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(32)

    defaultConfig {
        applicationId = "aueb.gr.nasiakouts.popularmovies"
        minSdkVersion(23)
        targetSdkVersion(32)
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.material:material:1.5.0")
    implementation("com.github.sayyam:carouselview:master-SNAPSHOT")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
}