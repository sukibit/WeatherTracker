import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.example.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            val localProperties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localProperties.load(localPropertiesFile.inputStream())
            }
            buildConfigField(
                "String",
                "OPENWEATHER_API_KEY",
                "\"${localProperties.getProperty("OPENWEATHER_API_KEY", "")}\""
            )
        }
        release {
            val localProperties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localProperties.load(localPropertiesFile.inputStream())
            }
            buildConfigField(
                "String",
                "OPENWEATHER_API_KEY",
                "\"${localProperties.getProperty("OPENWEATHER_API_KEY", "")}\""
            )
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

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }

    lint {
        disable += listOf(
            "NullSafeMutableLiveData",
            "RememberInComposition",
            "FrequentlyChangingValue"
        )
    }
}

dependencies {
    // Android & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.activity.compose)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Testing
    testImplementation(libs.junit)
}