plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-android-extensions")
    id("com.github.triplet.play") version (Versions.triplet)
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}


val major = 1
val minor = 6
val patch = 0

val generatedVersionName = String.format("%s%02d%02d", major, minor, patch)
val generateVersionCode = Integer.parseInt(generatedVersionName)


android {
    compileSdkVersion(Versions.compileVersion)
    buildToolsVersion(Versions.buildToolsVersion)

    defaultConfig {
        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)

        applicationId = "com.cvv.fanstaticapps.randomticker"

        versionCode = generateVersionCode
        versionName = generatedVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val fanstaticKeyAlias = project.findProperty("fanstatic_keyalias")
            println("Adding release config for production $fanstaticKeyAlias")

            if (fanstaticKeyAlias != null) {
                println("Adding release config for production")
                keyAlias = fanstaticKeyAlias as String
                keyPassword = project.property("fanstatic_keypassword") as String
                storeFile = file(project.property("fanstatic_file") as String)
                storePassword = project.property("fanstatic_storepassword") as String
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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

    implementation(AndroidLibs.livedata)
    implementation(AndroidLibs.viewmodel)
    implementation(AndroidLibs.extensions)
    implementation(AndroidLibs.preference)
    implementation(AndroidLibs.activity)
    implementation(AndroidLibs.fragment)
    implementation(AndroidLibs.coroutines)

    implementation(Firebase.crashlytics)

    implementation(Libs.hilt)
    implementation(Libs.hilt_viewmodel)
    implementation(Libs.timber)
    implementation(Libs.lottie)
    implementation(Libs.pulsator)
    implementation(Libs.ultimatePicker)

    implementation(Gms.oss)

    kapt(AndroidLibs.room_compiler)
    kapt(Libs.hilt_compile)
    kapt(Libs.hilt_viewmodel_compiler)

    testImplementation(TestLibs.mockito_core)
    testImplementation(TestLibs.junit)
    testImplementation(TestLibs.robolectric)
    testImplementation(TestLibs.mockito_kotlin)

    androidTestImplementation(AndroidLibs.uiautomator)
    androidTestImplementation(AndroidLibs.rules)
    androidTestImplementation(AndroidLibs.espresso_core)
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