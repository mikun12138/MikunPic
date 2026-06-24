package me.mikun.mikunpic.modules

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.origin
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import kotlin.time.Duration.Companion.minutes

fun Application.configureRateLimit() {
    install(RateLimit) {
        register(RateLimitName("with_ip")) {
            rateLimiter(
                limit = 60,
                refillPeriod = 1.minutes,
            )
            requestKey { call -> call.request.origin.remoteHost }
        }
    }
}
