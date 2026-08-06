package me.mikun.mikunpic.api

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import me.mikun.mikunpic.api.ApiTest.ohMyTest
import me.mikun.mikunpic.dto.data.Illustrator
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.dto.data.Platform
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import kotlin.test.Test

class Pic {
    @Test
    fun update() = ohMyTest {
        client.post(
            OhMyRouting.Manage.Pic.Update(),
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                OhMyRouting.Manage.Pic.Update.Body(
                    storageLabel = "sandbox1",
                    pic = Pic(
                        filename = "111.png",
                        illustrator = Illustrator(
                            name = "aaa",
                            platformKeyMap = mapOf(
                                Platform.Pixiv to "12138"
                            )
                        ),
                        tags = listOf("oo", "oooi")
                    ),
                ),
            )
        }
    }


    @Test
    fun random() = ohMyTest {
        client.post(
            OhMyRouting.Manage.Pic.Random(),
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                OhMyRouting.Manage.Pic.Random.Body(
                    storageLabels = listOf("sandbox1", "sandbox2", "sandbox3"),
                    count = 1,
                    illustratorIds = emptyList(),
                    tags = listOf("oo"),
                )
            )
        }.let {
            it.body<OhMyRouting.Manage.Pic.Random.Response>().let {
                println(it.pics)
            }
        }
    }
}