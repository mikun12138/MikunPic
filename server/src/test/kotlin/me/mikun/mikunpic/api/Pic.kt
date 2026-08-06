package me.mikun.mikunpic.api

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.streams.asInput
import kotlinx.serialization.json.Json
import me.mikun.mikunpic.api.ApiTest.ohMyTest
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.dto.data.Platform
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import kotlin.test.Test

class Pic {

    @Test
    fun upload() = ohMyTest {

        val picName = "rua.jpg"
        val response =
            client.post("/manage/pic/upload") {
//                bearerAuth(
//                    config.property("auth.bearer.token").getString(),
//                )
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            appendInput(
                                key = "file",
                                headers =
                                    Headers.build {
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            """form-data; name="file"; filename="$picName"""",
                                        )
                                    },
                            ) {
                                this::class.java.classLoader
                                    .getResourceAsStream("rua.jpg")
                                    .asInput()
                            }

                            append(
                                "storage_label",
                                "sandbox1",
                            )
                            append(
                                "pic",
                                Json.encodeToString(
                                    Pic(
                                        filename = picName,
                                        illustrator = me.mikun.mikunpic.dto.data.Illustrator(
                                            name = "mikun",
                                            platformKeyMap = mapOf(
                                                Platform.Pixiv to "mikun12138",
                                                Platform.Twitter to "mikun12139"
                                            )
                                        ),
                                        tags = listOf("12139")
                                    )
                                ),
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        ContentType.Application.Json.toString(),
                                    )
                                },
                            )
                        },
                    ),
                )
            }
    }

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
                        filename = "rua.jpg",
                        illustrator = me.mikun.mikunpic.dto.data.Illustrator(
                            name = "aaa",
                            platformKeyMap = mapOf(
                                Platform.Pixiv to "12138",
                                Platform.Twitter to "mikun12139"
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