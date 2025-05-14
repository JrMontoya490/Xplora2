plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.xplora2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.xplora2"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        multiDexEnabled = true

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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth:23.2.0")

    // Google Sign-In (para login con cuenta Google)
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // Retrofit (API REST)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Glide (imágenes)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // Maps
    implementation("com.google.android.gms:play-services-maps:19.2.0")

    // Gson
    implementation("com.google.code.gson:gson:2.11.0")

    // Picasso
    implementation("com.squareup.picasso:picasso:2.71828")
    
}


