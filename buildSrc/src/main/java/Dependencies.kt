object Versions {
    val kotlin = "1.2.61"
    val minSdkVersion = 21
    val targeSdkVersion = 28
    val compileVersion = 28
    val buildToolsVersion = "28.0.2"
}

object ArchitectureComponents {
    val version = "1.1.1"
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
    val supportLibVersion = "1.0.0-rc01"
    val material = "com.google.android.material:material:$supportLibVersion"
    val cardview = "androidx.cardview:cardview:$supportLibVersion"
    val appcompat = "androidx.appcompat:appcompat:$supportLibVersion"
    val preference = "androidx.preference:preference:$supportLibVersion"
    val recyclerview = "androidx.recyclerview:recyclerview:$supportLibVersion"

    val roomVersion = "2.0.0-beta01"
    val room_runtime = "androidx.room:room-runtime:$roomVersion"
    val room_rxjava2 = "androidx.room:room-rxjava2:$roomVersion"
    val room_compiler = "androidx.room:room-compiler:$roomVersion"

    val constraintLayoutVersion = "2.0.0-alpha1"
    val constraint_layout = "androidx.constraintlayout:constraintlayout:$constraintLayoutVersion"

    val uiautomator = "androidx.test.uiautomator:uiautomator:2.2.0-alpha3"
    val rules = "androidx.test:rules:1.1.0-alpha3"
    val espresso_core = "androidx.test.espresso:espresso-core:3.1.0-alpha3"
}

object Libs {
    val daggerVersion = "2.16"
    val dagger = "com.google.dagger:dagger:$daggerVersion"
    val dagger_android = "com.google.dagger:dagger-android:$daggerVersion"
    val dagger_android_support = "com.google.dagger:dagger-android-support:$daggerVersion"
    val dagger_compiler = "com.google.dagger:dagger-compiler:$daggerVersion"
    val dagger_android_processor = "com.google.dagger:dagger-android-processor:$daggerVersion"

    val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.0.2"
    val pulsator = "pl.bclogic:pulsator4droid:1.0.3"

    val grenadeVersion = "1.1.0"
    val grenade = "com.github.kobakei.grenade:library:$grenadeVersion"
    val grenade_processor = "com.github.kobakei.grenade:processor:$grenadeVersion"

    val timberVersion = "4.7.1"
    val timber = "com.jakewharton.timber:timber:$timberVersion"

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
