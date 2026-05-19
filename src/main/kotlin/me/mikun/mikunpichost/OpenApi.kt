package me.mikun.mikunpichost

import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.Application
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.routing.openapi.OpenApiDocSource
import io.ktor.server.routing.routing
import io.ktor.server.routing.routingRoot

fun Application.configureOpenApi() {
//    routing {
//        openAPI(path = "/", swaggerFile = "openapi/generated.json")
//    }
    routing {


        openAPI(path = "/") {
            info = OpenApiInfo(
                "mikun poc host",
                "0.0.1"
            )
            source = OpenApiDocSource.Routing {
                routingRoot.descendants()
            }
        }
    }
}
