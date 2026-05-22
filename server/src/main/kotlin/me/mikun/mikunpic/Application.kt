package me.mikun.mikunpic

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import io.ktor.server.plugins.origin
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import me.mikun.mikunpic.storage.PicStorage
import kotlin.time.Duration.Companion.minutes

fun main(args: Array<String>) {
    EngineMain
        .main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureAuth()
    configureRouting()
    configureOpenApi()
    configureDatabase()

    if (environment.config.property("api_only").getString() == "true") {
        install(ApiOnlyPlugin)
    }

    install(RateLimit) {
        register(RateLimitName("with_ip")) {
            rateLimiter(
                limit = 60,
                refillPeriod = 1.minutes
            )
            requestKey { call -> call.request.origin.remoteHost }
        }
    }

    install(XForwardedHeaders) {
        skipLastProxies(1)
    }

    install(CORS) {
        anyHost()
    }

    PicStorage.configure(this)
}
