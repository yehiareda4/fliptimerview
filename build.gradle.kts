buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven(url = "https://www.jitpack.io")
        configurations.all {
            resolutionStrategy {
                force("com.google.android.gms:play-services-location:21.0.1")
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}