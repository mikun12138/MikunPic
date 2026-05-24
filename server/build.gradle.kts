import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
}

group = "me.mikun.mikunpic"
version = "0.1.0"
application {
    mainClass = "me.mikun.mikunpic.ApplicationKt"
}

dependencies {
    api(projects.core)
//    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)

    implementation(libs.kotlin.asyncapi.ktor)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.forwarded.header)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.cors)

    implementation(libs.log4j.api)
    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4j2.impl)

    implementation(libs.cos.api)
    implementation(libs.cos.sts.api)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)

    implementation(libs.sqlite.jdbc)

    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(kotlin("test"))
}

configurations.all {
    exclude(group = "ch.qos.logback")
}

tasks.withType<Jar> {
    exclude("application.yaml")
}

tasks.withType<ShadowJar> {
    exclude("application.yaml")
}

ktor {
    fatJar {
        archiveFileName.set("mikunpic-$version.jar")
    }
}

