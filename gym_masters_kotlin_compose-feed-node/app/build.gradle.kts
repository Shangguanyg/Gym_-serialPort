import com.android.build.api.dsl.BuildType

// Helper function to make adding buildConfigFields cleaner
fun BuildType.buildConfigStringField(name: String, value: String) {
    buildConfigField("String", name, "\"$value\"")
}

fun BuildType.buildConfigBooleanField(name: String, value: Boolean) {
    buildConfigField("Boolean", name, value.toString())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.compose.compiler)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    alias(libs.plugins.kotlinx.serialization)
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}



android {
    namespace = "com.ganainy.gymmasterscompose"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ganainy.gymmasterscompose"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            abiFilters += listOf("x86_64", "arm64-v8a")
        }

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
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.txt",
                "META-INF/DEPENDENCIES",
                "META-INF/*.kotlin_module",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1"
            )
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigBooleanField("IS_APP_IN_DEBUG_MODE", false)
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigStringField( "SAMPLE_WORKOUT_IMAGE_URL", "\"https://firebasestorage.googleapis.com/v0/b/gym-masters.appspot.com/o/workouts%2F-Myv9QdZ0nK7WYV3JpQJk%2Fimage?alt=media&token=0e0f9f5b-9d3e-4e1e-94f1-6c9e0d7a5f0c\"")

            buildConfigBooleanField("IS_APP_IN_DEBUG_MODE", false)

            signingConfig = signingConfigs.getByName("debug")
        }

        // ---  custom build type here for testing purposes ---
        create("stagingDebug") {
            //Inherit from debug and override
            initWith(buildTypes.getByName("debug"))

            buildConfigStringField( "SAMPLE_WORKOUT_IMAGE_URL", "\"https://firebasestorage.googleapis.com/v0/b/gym-masters.appspot.com/o/workouts%2F-Myv9QdZ0nK7WYV3JpQJk%2Fimage?alt=media&token=0e0f9f5b-9d3e-4e1e-94f1-6c9e0d7a5f0c\"")

            // Override or add specific flags
            buildConfigBooleanField("IS_APP_IN_DEBUG_MODE", true)
        }
    }


    secrets {
        propertiesFileName = "secrets.properties"
        defaultPropertiesFileName = "local.defaults.properties"
    }

}



kapt {
    correctErrorTypes = true
    arguments {
        arg("dagger.processingX", "print")
    }
}

var skikoversion = if (project.hasProperty("skiko.version")) {
    project.properties["skiko.version"] as String
} else {
    "0.0.0-SNAPSHOT"
}

val skikoNativeX64 by configurations.creating
val skikoNativeArm64 by configurations.creating

val jniDir = "${projectDir.absolutePath}/src/main/jniLibs"

// TODO: filter .so files only.
val unzipTaskX64 = tasks.register("unzipNativeX64", Copy::class) {
    destinationDir = file("$jniDir/x86_64")
    from(skikoNativeX64.map { zipTree(it) })
}

val unzipTaskArm64 = tasks.register("unzipNativeArm64", Copy::class) {
    destinationDir = file("$jniDir/arm64-v8a")
    from(skikoNativeArm64.map { zipTree(it) })
}

dependencies {

    //navigation
    implementation(libs.androidx.navigation.compose)
    // Places
    implementation(libs.places)
    // Map
    implementation(libs.play.services.maps)
    // Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    //Material Icons
    implementation(libs.androidx.material.icons.extended)
    //Coil
    implementation(libs.coil.compose)

    //Dagger Hilt
    implementation(libs.hilt.android)
    implementation(libs.testng)
    implementation(libs.androidx.ui.test.junit4.android)
    implementation(libs.androidx.runner)
    implementation(libs.hilt.android.testing)
    implementation(libs.core)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.foundation)
    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.ext.junit)
    kapt(libs.dagger.hilt.android.compiler)
    implementation (libs.androidx.hilt.navigation.compose)

    //time ago
    implementation (libs.timeago)

    //  testing
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Optional but recommended for better assertions
    androidTestImplementation(libs.kotlintest.assertions)

    // Compose UI testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.6") {
        exclude(group = "androidx.test.ext", module = "junit")
        exclude(group = "androidx.test.espresso", module = "espresso-core")
    }

    // retrofit
    implementation (libs.squareup.retrofit)
    // gson converter
    implementation (libs.squareup.converter.gson)
    //okhttp
    implementation (libs.okhttp)
    //http interceptor
    implementation(libs.logging.interceptor)

    //glide for handling gifs
    implementation (libs.glide)
    ksp (libs.compiler)

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    debugImplementation(libs.androidx.ui.tooling)
    ksp(libs.androidx.room.compiler)
    implementation(libs.kotlinx.serialization.json)

    //Mockito  for unit tests
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    //  Robolectric framework, which provides a simulated Android environment for unit tests.
    testImplementation(libs.robolectric)
    //Default
    implementation(libs.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // to enable preview function in stagingDebug build type
    "stagingDebugImplementation"(libs.ui.tooling)

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.google.accompanist:accompanist-navigation-material:0.31.5-beta")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("net.mangolise:mango-game-sdk:latest")
//    implementation("net.minestom:minestom:2025.07.10b-1.21.7")

//    implementation ("com.badlogicgames.gdx:gdx-backend-android:1.13.1")
//    implementation ("com.badlogicgames.gdx:gdx-box2d:1.13.1")
//    implementation ("com.badlogicgames.gdx:gdx:1.13.1")
//    implementation ("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-armeabi-v7a")
//    implementation ("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-arm64-v8a")
//    implementation ("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-x86")
//    implementation ("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-x86_64")
//    implementation ("com.badlogicgames.gdx:gdx-box2d-platform:1.13.1:natives-armeabi-v7a")
//    implementation ("com.badlogicgames.gdx:gdx-box2d-platform:1.13.1:natives-arm64-v8a")
//    implementation ("com.badlogicgames.gdx:gdx-box2d-platform:1.13.1:natives-x86")
//    implementation ("com.badlogicgames.gdx:gdx-box2d-platform:1.13.1:natives-x86_64")

    implementation ("com.badlogicgames.gdx:gdx-backend-android:1.9.14")
    implementation ("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-armeabi")
    implementation ("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-armeabi-v7a")
    implementation ("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-x86")
    implementation ("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-arm64-v8a")
    implementation ("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-x86_64")
    implementation ("com.badlogicgames.gdx:gdx-box2d:1.9.14")
    implementation ("com.badlogicgames.gdx:gdx-box2d-platform:1.9.14:natives-armeabi")
    implementation ("com.badlogicgames.gdx:gdx-box2d-platform:1.9.14:natives-armeabi-v7a")
    implementation ("com.badlogicgames.gdx:gdx-box2d-platform:1.9.14:natives-x86")
    implementation ("com.badlogicgames.gdx:gdx-box2d-platform:1.9.14:natives-arm64-v8a")
    implementation ("com.badlogicgames.gdx:gdx-box2d-platform:1.9.14:natives-x86_64")
    implementation ("com.badlogicgames.gdx:gdx-freetype:1.9.14")
    implementation ("com.badlogicgames.gdx:gdx-freetype-platform:1.9.14:natives-armeabi")
    implementation ("com.badlogicgames.gdx:gdx-freetype-platform:1.9.14:natives-armeabi-v7a")
    implementation ("com.badlogicgames.gdx:gdx-freetype-platform:1.9.14:natives-x86")
    implementation ("com.badlogicgames.gdx:gdx-freetype-platform:1.9.14:natives-arm64-v8a")
    implementation ("com.badlogicgames.gdx:gdx-freetype-platform:1.9.14:natives-x86_64")
    implementation ("com.badlogicgames.gdx-controllers:gdx-controllers-core:2.2.2")
    implementation ("com.badlogicgames.gdx-controllers:gdx-controllers-android:2.2.2")
//    implementation(project(":pixelwheels-core"))

//    implementation("com.badlogicgames.gdx-controllers:gdx-controllers-core:2.2.2")
//    implementation("com.badlogicgames.gdx-controllers:gdx-controllers-android:2.2.2")
//    implementation ("com.badlogicgames.gdx:gdx-freetype:1.9.14")
//    // Android 平台需要添加 natives
//    implementation ("com.badlogicgames.gdx:gdx-freetype-platform:1.9.14:natives-armeabi-v7a")
//    implementation ("com.badlogicgames.gdx:gdx-freetype-platform:1.9.14:natives-arm64-v8a")
//    implementation ("com.badlogicgames.gdx:gdx-freetype-platform:1.9.14:natives-x86")
//    implementation ("com.badlogicgames.gdx:gdx-freetype-platform:1.9.14:natives-x86_64")

    implementation ("com.github.kostasdrakonakis:android-navigator:1.2.6")
    kapt ("com.github.kostasdrakonakis:android-navigator-compiler:1.2.6")
    implementation ("com.github.AAChartModel:AAChartCore-Kotlin:-SNAPSHOT")
    implementation ("com.google.android.material:material:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
    implementation("com.github.Breens-Mbaka:BeeTablesCompose:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

    implementation("org.jetbrains.skiko:skiko-android:${skikoversion}")

    api("io.github.libktx:ktx-app:1.9.11-b1")
    api("io.github.libktx:ktx-math:1.9.11-b1")
    api("io.github.libktx:ktx-freetype:1.9.11-b1")
    api("io.github.libktx:ktx-graphics:1.9.11-b1")
    api("io.github.libktx:ktx-box2d:1.9.11-b1")
    implementation("com.google.android.gms:play-services-ads:22.6.0")
    implementation("com.google.android.gms:play-services-ads:22.6.0")
    implementation("com.github.getActivity:ToastUtils:9.6")
    implementation("org.greenrobot:eventbus:3.2.0")
//    skikoNativeX64("org.jetbrains.skiko:skiko-android-runtime-x64:$skikoversion")
//    skikoNativeArm64("org.jetbrains.skiko:skiko-android-runtime-arm64:$skikoversion")

}

//kapt "com.github.kostasdrakonakis:android-navigator-compiler:1.2.6"

// Add version alignment strategy
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "androidx.test" ||
            requested.group == "androidx.test.ext" ||
            requested.group == "androidx.test.espresso") {
            requested.version?.let { useVersion(it) }
        }
    }

}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

