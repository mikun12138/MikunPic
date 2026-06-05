package me.mikun.mikunpic

import io.ktor.server.application.Application
import me.mikun.mikunpic.configureAuth
import me.mikun.mikunpic.configureRouting
import me.mikun.mikunpic.configureSerialization
import me.mikun.mikunpic.storage.PicStorage

fun Application.module() {
    configureAuth()
    configureResources()
    configureRouting()
    configureSerialization()
    configureDatabase()

    PicStorage.configure(this)
}