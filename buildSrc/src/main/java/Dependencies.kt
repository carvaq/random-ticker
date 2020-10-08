object Versions {
    const val kotlin = "1.4.10"
    const val minSdkVersion = 21
    const val targetSdkVersion = 30
    const val compileVersion = 30
    const val buildToolsVersion = "30.0.2"
    const val triplet = "3.0.0"
}

object Kotlin {
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
}

object Gms {
    const val services_plugin_version = "4.3.3"
    const val oss_plugin_version = "0.10.2"
    const val oss = "com.google.android.gms:play-services-oss-licenses:17.0.0"
}

object AndroidLibs {
    const val material = "com.google.android.material:material:1.2.1"
    const val cardview = "androidx.cardview:cardview:1.0.0"
    const val appcompat = "androidx.appcompat:appcompat:1.2.0"
    const val preference = "androidx.preference:preference:1.1.1"
    const val activity = "androidx.activity:activity-ktx:1.2.0-alpha08"
    const val fragment = "androidx.fragment:fragment-ktx:1.2.5"

    const val roomVersion = "2.2.5"
    const val room_runtime = "androidx.room:room-runtime:$roomVersion"
    const val room_ktx = "androidx.room:room-ktx:$roomVersion"
    const val room_compiler = "androidx.room:room-compiler:$roomVersion"

    private const val lifecycleVersion = "2.2.0"
    const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion"
    const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion"
    const val extensions = "androidx.lifecycle:lifecycle-extensions:$lifecycleVersion"

     const val coroutinesVersion = "1.3.9"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"

    private const val constraintLayoutVersion = "2.0.0"
    const val constraint_layout = "androidx.constraintlayout:constraintlayout:$constraintLayoutVersion"
}

object Libs {
    const val hiltVersion = "2.29-alpha"
    const val hilt = "com.google.dagger:hilt-android:$hiltVersion"
    const val hilt_compiler = "com.google.dagger:hilt-android-compiler:$hiltVersion"
    const val hilt_viewmodel = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha01"
    const val hilt_viewmodel_compiler = "androidx.hilt:hilt-compiler:1.0.0-alpha01"

    private const val timberVersion = "4.7.1"
    const val timber = "com.jakewharton.timber:timber:$timberVersion"

    private const val lottieVersion = "3.4.4"
    const val lottie = "com.airbnb.android:lottie:$lottieVersion"

    private const val ultimateVersion = "2.0.6"
    const val ultimatePicker = "com.github.DeweyReed:UltimateMusicPicker:$ultimateVersion"
}


object TestLibs {
    const val mockito_core = "org.mockito:mockito-core:3.1.0"
    const val junit = "junit:junit:4.13"
    const val robolectric = "org.robolectric:robolectric:4.3.1"
    const val mockito_kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"

    private const val testVersion = "1.3.0"

    const val test_core = "androidx.test:core-ktx:$testVersion"

    const val uiautomator = "androidx.test.uiautomator:uiautomator:2.2.0"

    private const val espressoVersion = "3.3.0"
    const val espresso_core = "androidx.test.espresso:espresso-core:$espressoVersion"
    const val espresso_idling = "androidx.test.espresso:espresso-idling-resource:$espressoVersion"
    const val espresso_contrib = "androidx.test.espresso:espresso-contrib:$espressoVersion"

    const val rules = "androidx.test:rules:$testVersion"
    const val junit_runner = "androidx.test:runner:$testVersion"
    const val orchestrator = "androidx.test:orchestrator:$testVersion"

    const val assertions_junit = "androidx.test.ext:junit-ktx:1.1.2"
    const val assertions_truth = "androidx.test.ext:truth:$testVersion"
    const val assertions_google_truth = "com.google.truth:truth:1.0.1"


    const val room_testing = "androidx.room:room-testing:${AndroidLibs.roomVersion}"
    const val hilt_testing = "com.google.dagger:hilt-android-testing:${Libs.hiltVersion}"
    const val coroutines_testing = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${AndroidLibs.coroutinesVersion}"

}

object Firebase {
    const val crashlytics = "com.google.firebase:firebase-crashlytics:17.2.1"
}
