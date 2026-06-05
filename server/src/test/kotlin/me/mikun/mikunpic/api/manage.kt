package me.mikun.mikunpic.api

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.testApplication
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import kotlin.test.Test

class Test {

    @Test
    fun test() = testApplication {
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

        client.get(
            OhMyRouting.Manage.Illustrator.Random(
                5
            )
        ).let {
            println(it.body<OhMyRouting.Manage.Illustrator.Random.Response>())
        }
//
//        client.post(
//            OhMyRouting.Manage.Illustrator.Create()
//        ) {
//            contentType(ContentType.Application.Json)
//
//            setBody(
//                OhMyRouting.Manage.Illustrator.Create.Body(
//                    "L"
//                )
//            )
//        }.let {
//            println(it.call.request.call)
//        }

        client.get(
            OhMyRouting.Manage.Pic.Random(
                5,
                null
            )
        ).let {
            println(it.body<OhMyRouting.Manage.Pic.Random.Response>())
        }


        client.get(
            OhMyRouting.Pic.Filename("1af516b5101fb83afe0bf5e1fa18108d.jpg")
        ).let {
            println(it.call.request.url)
        }


    }
}