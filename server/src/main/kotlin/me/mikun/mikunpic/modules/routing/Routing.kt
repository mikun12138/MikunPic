package me.mikun.mikunpic.modules.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.application
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.decodeFromString
import me.mikun.mikunpic.LocalMikunPicConfig
import me.mikun.mikunpic.dto.data.MikunPicConfig
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import me.mikun.mikunpic.onReloadConfig

fun Application.configureRouting() {
    routing {
        public()

        authenticate("bearer") {
            get<OhMyRouting.Manage.Config> {
                val config = call.receive<OhMyRouting.Manage.Config.Body>().mikunPicConfig

                LocalMikunPicConfig = config
                application.onReloadConfig()

                call.respond(HttpStatusCode.Created)
            }

            get("/auth") {
                call.respond(HttpStatusCode.OK)
            }

            manage()
        }
    }.let { println(it.children) }
}
