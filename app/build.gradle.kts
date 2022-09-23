import com.gcode.plugin.version.*

plugins{
    id("com.android.application")
    id("com.gcode.plugin.version")
}

android {
    compileSdk = Version.compile_sdk_version

    defaultConfig {
        applicationId = "com.gcode.music"
        minSdk = Version.min_sdk_version
        targetSdk = Version.target_sdk_version
        versionCode  = Version.version_code
        versionName = Version.version_name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    implementation(Libraries.permissionx)
    implementation(AndroidX.appcompat)
    implementation(AndroidX.constraintlayout)
    implementation(AndroidX.cardview)
    implementation(AndroidX.recyclerview)
    implementation(Libraries.vastadapter)
    testImplementation(Libraries.junit)
    androidTestImplementation(AndroidX.junit)
    androidTestImplementation(AndroidX.espresso_core)
}
