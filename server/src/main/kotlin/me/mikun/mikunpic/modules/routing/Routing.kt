package me.mikun.mikunpic.modules.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        public()
//        authenticate("bearer") {

        get("/auth") {
            call.respond(HttpStatusCode.OK)
        }
        manage()

//        }
    }
}
