package me.mikun.mikunpic.api

import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import me.mikun.mikunpic.api.ApiTest.ohMyTest
import me.mikun.mikunpic.dto.data.Platform
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import kotlin.test.Test

class Illustrator {

    @Test
    fun create() = ohMyTest {
        client.post(
            OhMyRouting.Manage.Illustrator.Create(),
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                OhMyRouting.Manage.Illustrator.Create.Body(
                    illustrator = me.mikun.mikunpic.dto.data.Illustrator(
                        name = "mikun",
                        platformKeyMap = mapOf(
                            Platform.Pixiv to "mikun12138",
                            Platform.Twitter to "mikun_12138"
                        )
                    )
                ),
            )
        }
    }
}