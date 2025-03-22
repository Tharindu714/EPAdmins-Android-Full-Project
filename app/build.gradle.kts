plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.deltacodex.epadmins"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.deltacodex.epadmins"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation ("com.google.firebase:firebase-firestore:24.9.1")
    implementation (libs.firebase.storage)

    implementation(libs.mpandroidchart)
    implementation (libs.material.v190)
    implementation (libs.appcompat.v160)
    implementation (libs.constraintlayout.v214)
    implementation (libs.imagepicker)

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    implementation ("com.google.firebase:firebase-auth:22.1.0")
    implementation ("com.google.android.gms:play-services-base:18.3.0")
    implementation ("com.squareup.picasso:picasso:2.8")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    implementation ("com.google.firebase:firebase-crashlytics:18.6.1")
    implementation ("com.google.firebase:firebase-analytics:21.3.0")
    implementation("com.google.android.material:material:1.11.0")

}