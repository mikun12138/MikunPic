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

        repeat(3) {
            val picName = "rua$it.jpg"
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
                                "sandbox$it",
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
                                                Platform.Twitter to "mikun_12138",
                                            )
                                        ),
                                        tags = listOf("a", "b", "y", "z")
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

    }

    @Test
    fun update() = ohMyTest {
        repeat(3) {

            client.post(
                OhMyRouting.Manage.Pic.Update(),
            ) {
                contentType(ContentType.Application.Json)
                setBody(
                    OhMyRouting.Manage.Pic.Update.Body(
                        storageLabel = "sandbox$it",
                        pic = Pic(
                            filename = "rua$it.jpg",
                            illustrator = me.mikun.mikunpic.dto.data.Illustrator(
                                name = "mikun",
                                platformKeyMap = mapOf(
                                    Platform.Pixiv to "mikun${12138 + it}",
                                    Platform.Twitter to "mikun_${12138 + it}",
                                )
                            ),
                            tags = listOf('a' + it, 'z' - it).map { it.toString() }
                        ),
                    ),
                )
            }
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
                    storageLabels = listOf("sandbox0", "sandbox1", "sandbox2"),
                    count = 2,
                    illustratorIds = listOf(1),
                    tags = listOf("z", "z"),
                )
            )
        }.let {
            it.body<OhMyRouting.Manage.Pic.Random.Response>().let {
                println(it.pics.map { it.filename })
            }
        }
    }
}