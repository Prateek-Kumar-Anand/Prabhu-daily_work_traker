plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.prabhu.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.prabhu.app"
        minSdk = 26
        targetSdk = 34
        // Always increases with every build so a new APK installs as an update.
        versionCode = (System.currentTimeMillis() / 60000L).toInt()
        versionName = "1.1"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Fixed signing key: every build (CI or local) is signed the same way, so
    // Android installs a new APK over the existing app and keeps its data.
    signingConfigs {
        create("studytrack") {
            storeFile = file("studytrack.jks")
            storePassword = "studytrack"
            keyAlias = "studytrack"
            keyPassword = "studytrack"
        }
    }

    lint {
        checkReleaseBuilds = false
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("studytrack")
        }
        release {
            signingConfig = signingConfigs.getByName("studytrack")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Applies the Compose baseline profiles on first launch (faster, smoother UI).
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
