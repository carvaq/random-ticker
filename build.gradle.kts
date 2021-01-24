// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
	repositories {
		google()
		jcenter()
		maven("https://plugins.gradle.org/m2/")
	}
	dependencies {
		classpath("com.android.tools.build:gradle:7.0.0-alpha04")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
		classpath("com.google.gms:google-services:${Gms.services_plugin_version}")
		classpath("com.google.android.gms:oss-licenses-plugin:${Gms.oss_plugin_version}")
		classpath("com.google.dagger:hilt-android-gradle-plugin:${Libs.hiltVersion}")
		classpath("com.google.firebase:firebase-crashlytics-gradle:2.4.1")
	}
}

allprojects {
	buildscript {
		repositories {
			google()
			jcenter()
		}
	}

	repositories {
		google()
		jcenter()
		maven("https://jitpack.io")
	}

	afterEvaluate {
		convention.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension>()?.apply {
			sourceSets.configureEach {
				languageSettings.progressiveMode = true
				languageSettings.enableLanguageFeature("NewInference")
			}
		}
	}
}

tasks.register<Delete>("clean").configure {
	delete(rootProject.buildDir)
}
