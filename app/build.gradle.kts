plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mobile2025s2_1_2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mobile2025s2_1_2"
        minSdk = 24
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
}

dependencies {
    // 🔥 Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    // 🔥 Firebase modules (너가 필요한 것만 선택)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    implementation("com.google.android.gms:play-services-auth:21.2.0")


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation("androidx.activity:activity:1.8.2")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}