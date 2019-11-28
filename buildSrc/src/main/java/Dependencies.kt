object Versions {
    const val kotlin = "1.3.50"
    const val minSdkVersion = 21
    const val targetSdkVersion = 29
    const val compileVersion = 29
    const val buildToolsVersion = "29.0.2"
    const val fabric = "1.28.0"
    const val androidGradle = "3.6.0-beta04"
    const val triplet = "2.6.1"
}

object Kotlin {
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
}

object Gms {
    const val services_plugin_version = "4.3.3"
    const val oss_plugin_version = "0.10.0"
    const val oss = "com.google.android.gms:play-services-oss-licenses:17.0.0"
}

object AndroidLibs {
    const val material = "com.google.android.material:material:1.0.0"
    const val cardview = "androidx.cardview:cardview:1.0.0"
    const val appcompat = "androidx.appcompat:appcompat:1.1.0"
    const val preference = "androidx.preference:preference:1.1.0"
    const val recyclerview = "androidx.recyclerview:recyclerview:1.0.0"

    private const val roomVersion = "2.2.1"
    const val room_runtime = "androidx.room:room-runtime:$roomVersion"
    const val room_rxjava2 = "androidx.room:room-rxjava2:$roomVersion"
    const val room_compiler = "androidx.room:room-compiler:$roomVersion"
    const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:2.1.0"
    const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.1.0"
    const val extensions = "androidx.lifecycle:lifecycle-extensions:2.1.0"

    const val work_version = "2.2.0"

    const val work = "androidx.work:work-runtime-ktx:$work_version"


    private const val constraintLayoutVersion = "1.1.3"
    const val constraint_layout = "androidx.constraintlayout:constraintlayout:$constraintLayoutVersion"

    const val uiautomator = "androidx.test.uiautomator:uiautomator:2.2.0"
    const val rules = "androidx.test:rules:1.2.0"
    const val espresso_core = "androidx.test.espresso:espresso-core:3.2.0"
}

object Libs {
    private const val daggerVersion = "2.25.2"
    const val dagger = "com.google.dagger:dagger:$daggerVersion"
    const val dagger_android = "com.google.dagger:dagger-android:$daggerVersion"
    const val dagger_android_support = "com.google.dagger:dagger-android-support:$daggerVersion"
    const val dagger_compiler = "com.google.dagger:dagger-compiler:$daggerVersion"
    const val dagger_android_processor = "com.google.dagger:dagger-android-processor:$daggerVersion"

    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
    const val pulsator = "pl.bclogic:pulsator4droid:1.0.3"

    private const val grenadeVersion = "1.1.0"
    const val grenade = "com.github.kobakei.grenade:library:$grenadeVersion"
    const val grenade_processor = "com.github.kobakei.grenade:processor:$grenadeVersion"

    private const val timberVersion = "4.7.1"
    const val timber = "com.jakewharton.timber:timber:$timberVersion"

    private const val lottieVersion = "3.2.0"
    const val lottie = "com.airbnb.android:lottie:$lottieVersion"

    private const val ultimateVersion = "2.0.6"
    const val ultimatePicker = "com.github.DeweyReed:UltimateMusicPicker:$ultimateVersion"


}

object TestLibs {
    const val mockito_core = "org.mockito:mockito-core:3.1.0"
    const val junit = "junit:junit:4.12"
    const val robolectric = "org.robolectric:robolectric:4.3.1"
    const val mockito_kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
}

object Firebase {
    const val crashlytics = "com.crashlytics.sdk.android:crashlytics:2.10.0"
}
