// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha11")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("com.google.gms:google-services:${Gms.services_plugin_version}")
        classpath("com.google.android.gms:oss-licenses-plugin:${Gms.oss_plugin_version}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Libs.hiltVersion}")
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
        maven("https://jitpack.io")
    }

}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}
