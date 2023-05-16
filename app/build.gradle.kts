plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.github.triplet.play") version "3.8.3"
    id("com.google.android.gms.oss-licenses-plugin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
}


val major = 1
val minor = 7
val patch = 4

val generatedVersionName = String.format("%s%02d%02d", major, minor, patch)
val generateVersionCode = Integer.parseInt(generatedVersionName)


android {

    compileSdk = 33
    compileSdkPreview = "UpsideDownCake"
    defaultConfig {

        testInstrumentationRunnerArguments += mapOf("clearPackageData" to "true")
        minSdk = 21
        targetSdk = 33
        targetSdkPreview = "UpsideDownCake"
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
}

dependencies {
    implementation("com.google.android.material:material:1.9.0")

    implementation("androidx.preference:preference-ktx:1.2.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-ktx:1.7.1")
    implementation("androidx.fragment:fragment-ktx:1.5.7")

    implementation("androidx.room:room-runtime:2.5.1")
    implementation("androidx.room:room-ktx:2.5.1")
    ksp("androidx.room:room-compiler:2.5.1")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    implementation("com.google.android.gms:play-services-oss-licenses:17.0.1")

    implementation("com.google.dagger:hilt-android:2.46.1")
    kapt("com.google.dagger:hilt-android-compiler:2.46.1")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.airbnb.android:lottie:6.0.0")


    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.10.2")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("androidx.test:runner:1.5.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("com.google.dagger:hilt-android-testing:2.46.1")
    kaptTest("com.google.dagger:hilt-android-compiler:2.46.1")

    androidTestImplementation("com.google.dagger:hilt-android-testing:2.46.1")
    androidTestImplementation("androidx.room:room-testing:2.5.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test.ext:truth:1.5.0")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.46.1")
    androidTestUtil("androidx.test:orchestrator:1.4.2")
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