package me.mikun.mikunpic

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import io.ktor.util.logging.Logger
import me.mikun.mikunpic.modules.configureApiOnly
import me.mikun.mikunpic.modules.configureAuth
import me.mikun.mikunpic.modules.configureCORS
import me.mikun.mikunpic.modules.configureDatabase
import me.mikun.mikunpic.modules.configureHTTP
import me.mikun.mikunpic.modules.configureOpenApi
import me.mikun.mikunpic.modules.configureRateLimit
import me.mikun.mikunpic.modules.configureResources
import me.mikun.mikunpic.modules.routing.configureRouting
import me.mikun.mikunpic.modules.configureSerialization
import me.mikun.mikunpic.storage.PicStorage

fun main(args: Array<String>) {
    EngineMain
        .main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureAuth()
    configureResources()
    configureRouting()
    configureOpenApi()
    configureDatabase()

    configureRateLimit()
    configureApiOnly()

    configureCORS()

    install(XForwardedHeaders) {
        skipLastProxies(1)
    }

    PicStorage.configure(this)
}
