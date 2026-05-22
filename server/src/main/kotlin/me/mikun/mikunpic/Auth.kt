package me.mikun.mikunpic

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic

fun Application.configureAuth() {
    val token = environment.config.property("token").getString()
    install(Authentication) {
        basic("basic") {
            realm = "basic"
            validate { credential ->
                if (credential.name == token && credential.password == token) {
                    UserIdPrincipal(credential.name)
                } else {
                    null
                }
            }
        }
    }

}