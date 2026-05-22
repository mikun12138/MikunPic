plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
}

group = "me.mikun.mikunpic"
version = "1.0.0"
application {
    mainClass = "me.mikun.mikunpic.ApplicationKt"
}

dependencies {
    api(projects.core)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}