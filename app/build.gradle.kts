plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    // id("kotlin-kapt")  // Temporarily disabled for hot reload
    kotlin("plugin.serialization") version "1.9.10"
}

android {
    namespace = "com.kawaidev.kawaime"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kawaidev.kawaime"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("${project.rootDir}/app/android.jks")
            storePassword = "12345678"
            keyAlias = "AndroidKey"
            keyPassword = "12345678"
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.fragnav)

    implementation(libs.okhttp)

    implementation(libs.androidx.recyclerview)

    implementation(libs.spacingitemdecoration)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.datasource)
    implementation(libs.androidx.media3.datasource.cronet)
    implementation(libs.androidx.media3.datasource.okhttp)

    implementation(libs.stfalconimageviewer)
    implementation(libs.glide)
    implementation(libs.glide.transformations)
    implementation(libs.androidx.swiperefreshlayout)
    // implementation(libs.icepick)  // Temporarily disabled for hot reload
    implementation(libs.androidx.preference)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // kapt(libs.icepick.processor)  // Temporarily disabled for hot reload

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}