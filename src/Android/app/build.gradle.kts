plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.sunmail"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sunmail"
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

//val defaultApiUrl = "http://10.0.2.2:8080/"
//
//fun getApiUrlFromLocalProperties(): String {
//    var foundUrl = defaultApiUrl
//
//    val propertiesFile = rootProject.file("local.properties")
//    if (!propertiesFile.exists()) {
//        return foundUrl
//    }
//
//    propertiesFile.forEachLine { line ->
//        if (line.contains("DEV_API_BASE_URL")) {
//            val value = line.split("=", limit = 2)[1].trim().removeSurrounding("\"")
//            if (value.isNotEmpty()) {
//                foundUrl = value
//            }
//        }
//    }
//    return foundUrl
//}
//
//val devApiUrl = getApiUrlFromLocalProperties()
//
//android {
//    namespace = "com.example.sunmail"
//    compileSdk = 35
//
//    defaultConfig {
//        applicationId = "com.example.sunmail"
//        minSdk = 24
//        targetSdk = 35
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//        isCoreLibraryDesugaringEnabled = true
//    }
//
//    buildFeatures {
//        buildConfig = true
//    }
//
//    flavorDimensions += "environment"
//
//    productFlavors {
//        create("development") {
//            dimension = "environment"
//            buildConfigField("String", "API_BASE_URL", "\"$devApiUrl\"")
//        }
//        create("production") {
//            dimension = "environment"
//            buildConfigField("String", "API_BASE_URL", "\"$defaultApiUrl\"")
//        }
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}