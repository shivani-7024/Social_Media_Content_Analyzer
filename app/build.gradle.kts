plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.socialmediacontentanalyzer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.socialmediacontentanalyzer"
        minSdk = 24
        targetSdk = 34
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // File chooser compatibility
    implementation("androidx.fragment:fragment:1.5.5")

    // ML Kit Text Recognition
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // iText library for PDF text extraction
    implementation("com.itextpdf:itext7-core:7.1.15")

    //volley
    implementation ("com.android.volley:volley:1.2.1")

    //lottie
    implementation ("com.airbnb.android:lottie:3.4.0")
}