plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.navigation)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.dat.bookstore_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dat.bookstore_app"
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
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.room.runtime.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    // Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // Navigation
    val nav_version = "2.7.6"
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Coil
    implementation("io.coil-kt.coil3:coil:3.2.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.2.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.56.1")
    kapt("com.google.dagger:hilt-android-compiler:2.56.1")
    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    /// Networking
    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:3.0.0")
    implementation ("com.squareup.retrofit2:converter-moshi:3.0.0")
    // Moshi
    implementation ("com.squareup.moshi:moshi:1.15.2")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
    // Logging
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.16")

    //Paging 3
    implementation ("androidx.paging:paging-runtime:3.2.1")

    // Swipe
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Kotlin Serialization
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // FlexboxLayout
    implementation ("com.google.android.flexbox:flexbox:3.0.0")

    //Shimmer
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    //CustomTabsIntent
    implementation ("androidx.browser:browser:1.6.0")

    // Lottie (Animation)
    implementation("com.airbnb.android:lottie:6.4.0")

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    implementation ("com.github.yalantis:ucrop:2.2.8")

    implementation ("com.google.android.gms:play-services-auth:21.0.0")
}