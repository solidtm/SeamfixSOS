plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.solid.seamfixsos"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.solid.seamfixsos"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug{
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            android.buildFeatures.buildConfig = true
            buildConfigField("boolean", "DEBUG", "true")
            buildConfigField(type = "String", name = "BASE_URL", value = "\"http://dummy.restapiexample.com\"")
        }
        release {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            buildConfigField("boolean", "DEBUG", "false")
            buildConfigField(type = "String", name = "BASE_URL", value = "\"http://dummy.restapiexample.com\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

//  Common
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //    Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)

//    Retrofit
    implementation(libs.retrofit)
    implementation(libs.moshi)
    implementation(libs.moshi.converter)
    implementation(libs.okhttp3)
    implementation(libs.logging)

//    Glide
    implementation(libs.glide)

//    Material dialog
    implementation(libs.material.dialog)
    implementation(libs.material.bottom.sheet)

//    Play services
    implementation(libs.play.services.location)

//    Coroutines
    implementation(libs.kotlinx.coroutines.android)


//    Testing
    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.mockK)

    androidTestImplementation(libs.mockK)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}