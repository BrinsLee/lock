/*import com.google.protobuf.gradle.ProtobufConvention
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc*/
import com.google.protobuf.gradle.*


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("dagger.hilt.android.plugin")
    id ("kotlin-kapt")
    id ("kotlin-parcelize")
    id ("androidx.navigation.safeargs.kotlin")
    id ("com.google.protobuf") version "0.9.4"

}

android {
    namespace = "com.lock.locksmith"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lock.locksmith"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    sourceSets {
        getByName("main") {
            java {
                srcDir("src/main/java")
            }
            proto {
                srcDir("src/main/proto")
            }
        }

    }

}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.4"
    }
/*    plugins {
        create("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:1.55.1" }
    }*/
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {

                }
                create("cpp") {

                }
            }
            /*task.plugins {
                create("grpc") {
                    option("lite")
                }
            }*/
        }
    }
}




dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // 添加 libs 文件夹下的所有 .aar 文件
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.hilt.android)
    implementation(project(":appthemehelper"))
    implementation (project(":magicindicator"))
    implementation (project(":result"))
    // implementation (files("libs/blurlib-release.aar"))
    kapt(libs.hilt.android.compiler)

    implementation (libs.stream.log)

    implementation(libs.recyclerview)

    implementation (libs.material.v1100)

    implementation (libs.androidx.biometric)


    implementation (libs.androidx.navigation.runtime.ktx)
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
    implementation(libs.material.v140)

    implementation (libs.androidx.preference.ktx)

    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation (libs.androidx.lifecycle.common.java8)
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.androidx.lifecycle.extensions)
    implementation (libs.androidx.swiperefreshlayout)


    implementation (libs.insetter)
    implementation (libs.material.cab)
    implementation (libs.core)
    implementation (libs.input)
    implementation (libs.color)


    implementation (libs.base.recyclerView.adapter.helper4)
    implementation (libs.gson)

    implementation (libs.protobuf.java)
    // implementation (libs.protoc)

    implementation (libs.customactivityoncrash)
    implementation (libs.bcprov.jdk15on)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}