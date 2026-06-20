package me.mikun.mikunpic.modules

import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.Application
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.routing.openapi.OpenApiDocSource
import io.ktor.server.routing.routing
import io.ktor.server.routing.routingRoot

// TODO:: remove
fun Application.configureOpenApi() {
    routing {
        openAPI(path = "/") {
            info =
                OpenApiInfo(
                    "mikun pic",
                    "0.0.1",
                )
            source =
                OpenApiDocSource.Routing {
                    routingRoot.descendants()
                }
        }
    }
}
