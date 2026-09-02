package me.mikun.mikunpic.api

import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import me.mikun.mikunpic.api.ApiTest.ohMyTest
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import kotlin.test.Test

class Tag {

    @Test
    fun create() = ohMyTest {
        val tags = generateSequence('a') { it + 1 }

        tags.take(26).forEach { tag ->
            client.post(
                OhMyRouting.Manage.Tag.Create(),
            ) {
                contentType(ContentType.Application.Json)

                setBody(
                    OhMyRouting.Manage.Tag.Create.Body(
                        tag.toString(),
                    ),
                )
            }
        }
    }
}
