plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.github.triplet.play") version (Versions.triplet)
    id("com.google.android.gms.oss-licenses-plugin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.github.ben-manes.versions") version (Versions.bennames)
    id("se.patrikerdes.use-latest-versions") version "0.2.17"
}


val major = 1
val minor = 6
val patch = 5

val generatedVersionName = String.format("%s%02d%02d", major, minor, patch)
val generateVersionCode = Integer.parseInt(generatedVersionName)


android {

    compileSdk = Versions.compileVersion
    buildToolsVersion = Versions.buildToolsVersion
    defaultConfig {

        testInstrumentationRunnerArguments += mapOf("clearPackageData" to "true")
        minSdk = Versions.minSdkVersion
        targetSdk = Versions.targetSdkVersion
        applicationId = "com.cvv.fanstaticapps.randomticker"

        versionCode = generateVersionCode
        versionName = generatedVersionName

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    implementation(Kotlin.stdlib)

    implementation(AndroidLibs.material)
    implementation(AndroidLibs.cardview)
    implementation(AndroidLibs.appcompat)
    implementation(AndroidLibs.constraint_layout)
    implementation(AndroidLibs.room_runtime)
    implementation(AndroidLibs.room_ktx)

    implementation(AndroidLibs.lifecycle_livedata)
    implementation(AndroidLibs.lifecycle_viewmodel)
    implementation(AndroidLibs.lifecycle_extensions)
    implementation(AndroidLibs.lifecycle_common)
    implementation(AndroidLibs.preference)
    implementation(AndroidLibs.activity)
    implementation(AndroidLibs.fragment)
    implementation(AndroidLibs.coroutines)

    implementation(Libs.hilt)
    implementation(Libs.hilt_viewmodel)
    implementation(Libs.timber)
    implementation(Libs.lottie)
    implementation(Libs.ultimatePicker)

    implementation(Gms.oss)

    kapt(AndroidLibs.room_compiler)
    kapt(Libs.hilt_compiler)
    kapt(Libs.hilt_viewmodel_compiler)

    testImplementation(TestLibs.mockito_core)
    testImplementation(TestLibs.junit)
    testImplementation(TestLibs.test_core)
    testImplementation(TestLibs.robolectric)
    testImplementation(TestLibs.mockito_kotlin)
    testImplementation(TestLibs.assertions_junit)
    testImplementation(TestLibs.hilt_testing)
    testImplementation(TestLibs.coroutines_testing)
    testImplementation(TestLibs.assertions_truth)
    testImplementation(TestLibs.assertions_google_truth)
    kaptTest(Libs.hilt_compiler)

    androidTestImplementation(TestLibs.room_testing)
    androidTestImplementation(TestLibs.uiautomator)
    androidTestImplementation(TestLibs.test_core)
    androidTestImplementation(TestLibs.rules)
    androidTestImplementation(TestLibs.junit_runner)
    androidTestImplementation(TestLibs.espresso_core)
    androidTestImplementation(TestLibs.espresso_idling)
    androidTestImplementation(TestLibs.espresso_contrib)
    androidTestImplementation(TestLibs.assertions_junit)
    androidTestImplementation(TestLibs.assertions_truth)
    androidTestImplementation(TestLibs.assertions_google_truth)
    androidTestImplementation(TestLibs.hilt_testing)
    kaptAndroidTest(Libs.hilt_compiler)
    androidTestUtil(TestLibs.orchestrator)
}

play {
    println("Configuring play plugin ${file("$rootDir/play_store_secret.json").exists()}")
    if (file("$rootDir/play_store_secret.json").exists()) {
        defaultToAppBundles.set(true)
        serviceAccountCredentials.set(file("$rootDir/play_store_secret.json"))
    } else {
        enabled.set(false)
    }
}

kapt {
    generateStubs = false
    correctErrorTypes = true
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
        arg("room.expandProjection", "true")
    }
}
repositories {
    mavenCentral()
}