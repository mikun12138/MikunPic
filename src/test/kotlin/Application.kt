package me.mikun

import io.ktor.server.application.Application
import me.mikun.mikunpichost.configureRouting
import me.mikun.mikunpichost.configureSerialization

fun Application.module() {
    configureRouting()
    configureSerialization()
}