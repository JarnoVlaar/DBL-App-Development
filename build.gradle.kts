// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("jacoco")
    id("org.sonarqube") version "4.3.1.3277"
}

apply {
    from("sonarqube.gradle")
    from("jacoco.gradle")
}