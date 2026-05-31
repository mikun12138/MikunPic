package me.mikun.mikunpic

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.bearer

fun Application.configureAuth() {
    val token = environment.config.property("auth.bearer.token").getString()
    install(Authentication) {
        bearer("bearer") {
            realm = "/manage"
            authenticate {
                    tokenCredential ->
                if (tokenCredential.token == token) {
                    UserIdPrincipal("")
                } else {
                    null
                }
            }
        }
    }
}