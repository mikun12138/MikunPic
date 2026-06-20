package me.mikun.mikunpic

import io.ktor.server.application.Application
import me.mikun.mikunpic.modules.configureAuth
import me.mikun.mikunpic.modules.routing.configureRouting
import me.mikun.mikunpic.modules.configureSerialization
import me.mikun.mikunpic.modules.configureDatabase
import me.mikun.mikunpic.modules.configureResources
import me.mikun.mikunpic.storage.PicStorage

fun Application.module() {
    configureAuth()
    configureResources()
    configureRouting()
    configureSerialization()
    configureDatabase()

    PicStorage.configure(this)
}
