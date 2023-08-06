// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.6")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.46.1")
    }
}

allprojects {
    buildscript {
        repositories {
            google()
        }
    }

    repositories {
        google()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}