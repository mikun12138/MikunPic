package me.mikun.mikunpic.modules

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.request.uri
import io.ktor.server.response.respondText
import kotlin.text.contains

fun Application.configureApiOnly() {
    if (environment.config.property("api_only").getString() == "true") {
        install(ApiOnlyPlugin)
    }
}

private val ApiOnlyPlugin =
    createApplicationPlugin(name = "ApiOnlyPlugin") {
        onCall { call ->
            val isDocPath =
                call.request.uri.contains("openapi") ||
                    call.request.uri.contains("swagger")

            val accept = call.request.headers["Accept"] ?: ""

            val isBrowser = accept.contains("text/html")

            if (!isDocPath && isBrowser) {
                call.respondText(
                    "Access Denied: API calls only.",
                    status = HttpStatusCode.Forbidden,
                )
            }
        }
    }
