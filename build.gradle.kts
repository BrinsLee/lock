// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {


    dependencies {
        classpath  (libs.dagger.hilt.android.gradle.plugin)
        classpath  (libs.androidx.navigation.safe.args.gradle.plugin)
        // classpath (libs.protobuf.gradle.plugin)
    }
}


plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}