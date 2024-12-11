plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.helloworld"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.helloworld"
        minSdk = 26
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Retrofit 라이브러리
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Retrofit용 Gson 컨버터
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp 클라이언트
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // OkHttp 로깅 인터셉터 (옵션)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation ("com.github.bumptech.glide:glide:4.13.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}