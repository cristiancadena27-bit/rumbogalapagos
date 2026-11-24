plugins {
    id("com.android.application")
}

android {
    namespace = "ec.familia.rumbogalapagos"
    compileSdk = 34

    defaultConfig {
        applicationId = "ec.familia.rumbogalapagos"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
