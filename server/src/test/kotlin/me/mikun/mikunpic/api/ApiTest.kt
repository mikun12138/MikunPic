package me.mikun.mikunpic.api

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication

object ApiTest {
    internal fun ohMyTest(
        block: suspend ApplicationTestBuilder.() -> Unit,
    ) = testApplication {
        val config = ApplicationConfig("application.yaml")
        environment {
            this.config = ApplicationConfig("application.yaml")
        }
        client = createClient {
            install(Resources)

            install(ContentNegotiation) {
                json()
            }
        }

        block()
    }
}
