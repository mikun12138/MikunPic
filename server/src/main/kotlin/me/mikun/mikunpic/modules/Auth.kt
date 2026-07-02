package me.mikun.mikunpic.modules

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.bearer
import me.mikun.mikunpic.LocalMikunPicConfig
import me.mikun.mikunpic.dto.data.MikunPicConfig

fun Application.configureAuth() {
    install(Authentication) {
        when (LocalMikunPicConfig.auth) {
            is MikunPicConfig.Auth.Bearer -> {
                (LocalMikunPicConfig.auth as MikunPicConfig.Auth.Bearer).let { bearer ->
                    val token = bearer.token
                    this@configureAuth.log.info("token: $token")
                }

                bearer("bearer") {
                    realm = "/manage"
                    authenticate { tokenCredential ->
                        (LocalMikunPicConfig.auth as MikunPicConfig.Auth.Bearer).let { bearer ->
                            val token = bearer.token
                            if (tokenCredential.token == token) {
                                UserIdPrincipal("")
                            } else {
                                null
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }
}
