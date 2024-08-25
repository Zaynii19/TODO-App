// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

    id ("com.android.library") version  "8.5.2" apply false
    id ("com.google.devtools.ksp") version  "1.9.0-1.0.13" apply false
}