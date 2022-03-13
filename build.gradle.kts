buildscript {

    repositories {
        google()
        mavenCentral()
        maven(uri("https://jitpack.io"))
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(uri("https://jitpack.io"))
    }
}

tasks.register<Delete>("clean"){
    delete(rootProject.buildDir)
}
