package me.mikun.mikunpic

import com.asyncapi.kotlinasyncapi.context.service.AsyncApiExtension
import com.asyncapi.kotlinasyncapi.ktor.AsyncApiPlugin
import io.ktor.server.application.Application
import io.ktor.server.application.install

fun Application.configureHTTP() {
    install(AsyncApiPlugin) {
        extension =
            AsyncApiExtension.builder {
                info {
                    title("Mikun Pic")
                    version("0.0.1")
                }
            }
    }
}
