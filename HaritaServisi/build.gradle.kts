// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}

buildscript{
    repositories {
        google()
        mavenCentral()
    }
    dependencies{
        classpath("com.android.tools.build:gradle:8.10.1")
        classpath("com.google.gms:google-services:4.4.1")
    }
}