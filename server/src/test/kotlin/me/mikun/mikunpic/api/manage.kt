package me.mikun.mikunpic.api

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.delay
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import kotlin.sequences.forEach
import kotlin.test.Test
import kotlin.test.assertEquals

class Test {

    private fun ohMyTest(
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

    @Test
    fun test() = ohMyTest {
        client.get(
            OhMyRouting.Manage.Illustrator.Random(
                5,
            ),
        ).let {
            println(it.body<OhMyRouting.Manage.Illustrator.Random.Response>())
        }

        client.get(
            OhMyRouting.Pic.Filename("1af516b5101fb83afe0bf5e1fa18108d.jpg"),
        ).let {
            println(it.call.request.url)
        }
    }

    @Test
    fun testIllustratorSearch() = ohMyTest {
        client.get(
            OhMyRouting.Manage.Illustrator.Search(
                count = 5,
                keyword = "a",
                page = 0,
            ),
        ).let {
            println(it.body<OhMyRouting.Manage.Illustrator.Search.Response>())
        }
    }

    @Test
    fun testPicUpdate() = ohMyTest {
        client.post(
            OhMyRouting.Manage.Pic.Update(),
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                OhMyRouting.Manage.Pic.Update.Body(
                    pic = Pic(
                        filename = "01_着衣版.png",
                        illustrator = "aa",
                    ),
                ),
            )
        }.let {
            println(it.status)
        }
    }

    @Test
    fun testIllustratorCreate() = ohMyTest {
        val illustrators = generateSequence("a") { it + "a" }

        illustrators.take(32).forEach { illustrator ->
            client.post(
                OhMyRouting.Manage.Illustrator.Create(),
            ) {
                contentType(ContentType.Application.Json)

                setBody(
                    OhMyRouting.Manage.Illustrator.Create.Body(
                        illustrator,
                    ),
                )
            }
        }
    }

    @Test
    fun testTagCreate() = ohMyTest {
        val tags = generateSequence("oi") { it + "oi" }

        tags.take(16).forEach { tag ->
            client.post(
                OhMyRouting.Manage.Tag.Create(),
            ) {
                contentType(ContentType.Application.Json)

                setBody(
                    OhMyRouting.Manage.Tag.Create.Body(
                        tag,
                    ),
                )
            }
        }
    }

    @Test
    fun testPicRandom() = ohMyTest {
        client.get(
            OhMyRouting.Manage.Pic.Random(
                114514,
                listOf(),
                emptyList(),
            ),
        ).let {
            println(it.call.request.url)
            it.body<OhMyRouting.Manage.Pic.Random.Response>().let {
                println(it.pics.map { it.illustrator })
            }
        }
    }

    @Test
    fun testBackup() = ohMyTest {
        client.post(
            OhMyRouting.Manage.Backup(),
        )
    }

    @Test
    fun testSync() = ohMyTest {
        // TODO:: fix 1 min limit
        client.post(
            OhMyRouting.Manage.Sync(),
        )

        delay(1000 * 60 * 5)
    }
}
