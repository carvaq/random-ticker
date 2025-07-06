import com.github.triplet.gradle.androidpublisher.ResolutionStrategy

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.tripletPlay)
    alias(libs.plugins.oss.licenses)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
}


val major = 2
val minor = 1
val patch = 0

android {

    compileSdk = 36
    defaultConfig {
        testInstrumentationRunnerArguments += mapOf("clearPackageData" to "true")
        minSdk = 26
        targetSdk = 36
        applicationId = "com.cvv.fanstaticapps.randomticker"

        versionCode = 10706
        versionName = String.format("%s%02d%02d", major, minor, patch)

        testInstrumentationRunner = "com.fanstaticapps.randomticker.TickerTestRunner"
    }

    signingConfigs {
        create("release") {
            val fanstaticKeyAlias = System.getenv("RELEASE_KEY_ALIAS")
            println("Adding release config for production $fanstaticKeyAlias")

            if (fanstaticKeyAlias != null) {
                println("Adding release config for production")
                keyAlias = fanstaticKeyAlias
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
                storeFile = file("$rootDir/fanstaticapps.keystore")
                storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro"
                )
            )
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
        }
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        animationsDisabled = true
    }

    sourceSets {
        getByName("androidTest").assets.srcDirs("$projectDir/schemas")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    namespace = "com.fanstaticapps.randomticker"

    room {
        schemaDirectory("$projectDir/schemas")
    }
}
dependencies {
    implementation(libs.material)

    implementation(libs.appcompat)
    implementation(libs.activity)

    ksp(libs.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.extensions)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.oss.licenses)

    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)

    implementation(libs.accompanist.permissions)

    implementation(libs.timber)

    implementation(platform(libs.compose.bom))
    implementation(libs.material3)
    implementation(libs.foundation)
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material.icons.extended)
    implementation(libs.material3.window.size)
    implementation(libs.ui.viewbinding)
    debugImplementation(libs.ui.tooling)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.roblectric)

    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.android.junit)
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}

play {
    println("Configuring play plugin ${file("$rootDir/play_store_secret.json").exists()}")
    if (file("$rootDir/play_store_secret.json").exists()) {
        defaultToAppBundles.set(true)
        serviceAccountCredentials.set(file("$rootDir/play_store_secret.json"))
        resolutionStrategy.set(ResolutionStrategy.AUTO)
    } else {
        enabled.set(false)
    }
}