object Versions {
    const val kotlin = "1.2.71"
    private const val minSdkVersion = 21
    private const val targeSdkVersion = 28
    private const val compileVersion = 28
    private const val buildToolsVersion = "28.0.3"
}

object ArchitectureComponents {
    private const val version = "1.1.1"
    val room = "android.arch.persistence.room:runtime:$version"
    val rx = "android.arch.persistence.room:rxjava2:$version"
}

object Kotlin {
    val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
}

object Gms {
    val services_plugin_version = "4.0.1"
    val oss_plugin_version = "0.9.2"
    val oss = "com.google.android.gms:play-services-oss-licenses:15.0.1"
}

object AndroidLibs {
    private const val supportLibVersion = "1.0.0"
    val material = "com.google.android.material:material:$supportLibVersion"
    val cardview = "androidx.cardview:cardview:$supportLibVersion"
    val appcompat = "androidx.appcompat:appcompat:$supportLibVersion"
    val preference = "androidx.preference:preference:$supportLibVersion"
    val recyclerview = "androidx.recyclerview:recyclerview:$supportLibVersion"

    private const val lifecycleVersion = "2.0.0-beta01"
    val room_runtime = "androidx.room:room-runtime:$lifecycleVersion"
    val room_rxjava2 = "androidx.room:room-rxjava2:$lifecycleVersion"
    val room_compiler = "androidx.room:room-compiler:$lifecycleVersion"
    val livedata = "androidx.lifecycle:lifecycle-livedata:$lifecycleVersion"
    val viewmodel = "androidx.lifecycle:lifecycle-viewmodel:$lifecycleVersion"
    val extensions = "androidx.lifecycle:lifecycle-extensions:$lifecycleVersion"

    private const val constraintLayoutVersion = "2.0.0-alpha1"
    val constraint_layout = "androidx.constraintlayout:constraintlayout:$constraintLayoutVersion"

    val uiautomator = "androidx.test.uiautomator:uiautomator:2.2.0-alpha3"
    val rules = "androidx.test:rules:1.1.0-alpha3"
    val espresso_core = "androidx.test.espresso:espresso-core:3.1.0-alpha3"
}

object Libs {
    private const val daggerVersion = "2.16"
    val dagger = "com.google.dagger:dagger:$daggerVersion"
    val dagger_android = "com.google.dagger:dagger-android:$daggerVersion"
    val dagger_android_support = "com.google.dagger:dagger-android-support:$daggerVersion"
    val dagger_compiler = "com.google.dagger:dagger-compiler:$daggerVersion"
    val dagger_android_processor = "com.google.dagger:dagger-android-processor:$daggerVersion"

    val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.0.2"
    val pulsator = "pl.bclogic:pulsator4droid:1.0.3"

    private const val grenadeVersion = "1.1.0"
    val grenade = "com.github.kobakei.grenade:library:$grenadeVersion"
    val grenade_processor = "com.github.kobakei.grenade:processor:$grenadeVersion"

    private const val timberVersion = "4.7.1"
    val timber = "com.jakewharton.timber:timber:$timberVersion"

    private const val lottieVersion = "2.6.0-beta19"
    val lottie = "com.airbnb.android:lottie:$lottieVersion"

    private const val ultimateVersion = "2.0.2"
    val ultimatePicker = "com.github.DeweyReed:UltimateMusicPicker:$ultimateVersion"

    val exoPlayer = "com.google.android.exoplayer:exoplayer-core:2.9.1"

}

object TestLibs {
    val mockito_core = "org.mockito:mockito-core:2.19.0"
    val junit = "junit:junit:4.12"
    val robolectric = "org.robolectric:robolectric:3.8"
    val mockito_kotlin = "com.nhaarman:mockito-kotlin-kt1.1:1.6.0"
}

object Firebase {
    val crashlytics = "com.crashlytics.sdk.android:crashlytics:2.9.4"
}
