plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.github.triplet.play") version "3.7.0"
    id("com.google.android.gms.oss-licenses-plugin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}


val major = 1
val minor = 7
val patch = 0

val generatedVersionName = String.format("%s%02d%02d", major, minor, patch)
val generateVersionCode = Integer.parseInt(generatedVersionName)


android {

    compileSdk = 33
    buildToolsVersion = "33.0.0"
    defaultConfig {

        testInstrumentationRunnerArguments += mapOf("clearPackageData" to "true")
        minSdk = 21
        targetSdk = 33
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

    namespace = "com.fanstaticapps.randomticker"
    testNamespace = "your.package.name"
}

dependencies {
    implementation("com.google.android.material:material:1.6.1")

    implementation("androidx.preference:preference-ktx:1.2.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.5.0")
    implementation("androidx.activity:activity-ktx:1.5.1")
    implementation("androidx.fragment:fragment-ktx:1.5.2")

    implementation("androidx.room:room-runtime:2.4.3")
    implementation("androidx.room:room-ktx:2.4.3")
    kapt("androidx.room:room-compiler:2.4.3")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.5.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation("com.google.android.gms:play-services-oss-licenses:17.0.0")

    implementation("com.google.dagger:hilt-android:2.43.2")
    kapt("com.google.dagger:hilt-android-compiler:2.43.2")
    kapt("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.airbnb.android:lottie:5.2.0")


    testImplementation("org.mockito:mockito-core:4.7.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.8.2")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("androidx.test:core-ktx:1.4.0")
    testImplementation("androidx.room:room-testing:2.4.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("com.google.truth:truth:1.1.3")
    kaptTest("com.google.dagger:hilt-android-compiler:2.43.2")

    androidTestImplementation("com.google.dagger:hilt-android-testing:2.43.2")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    androidTestImplementation("androidx.test.ext:truth:1.4.0")
    androidTestImplementation("com.google.truth:truth:1.1.3")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.43.2")
    androidTestUtil("androidx.test:orchestrator:1.4.1")
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