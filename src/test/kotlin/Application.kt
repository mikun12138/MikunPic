package me.mikun

import io.ktor.server.application.Application
import me.mikun.mikunpic.configureAuth
import me.mikun.mikunpic.configureRouting
import me.mikun.mikunpic.configureSerialization

fun Application.module() {
    configureAuth()
    configureRouting()
    configureSerialization()

}