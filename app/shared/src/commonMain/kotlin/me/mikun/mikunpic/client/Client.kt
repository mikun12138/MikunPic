package me.mikun.mikunpic.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.Buffer
import me.mikun.mikunpic.Config
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.dto.data.api.OhMyRouting

var localToken: String? = null

object Client {
    val baseUrl = Config.server

    val httpClient =
        HttpClient {
            install(HttpRequestRetry) {
                maxRetries = 3

                exponentialDelay()

                retryOnExceptionIf { _, _ ->
                    true
                }
            }

            install(DefaultRequest) {
                url(baseUrl)
                header(
                    HttpHeaders.CacheControl,
                    "no-cache",
                )
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        localToken?.let { BearerTokens(it, null) }
                    }
                }
            }

            install(Resources)

            install(ContentNegotiation) {
                json()
            }
        }

    suspend fun uploadPic(
        picName: String,
        picBytes: ByteArray,
    ) {
        httpClient.submitFormWithBinaryData(
            url = "/manage/pic/upload",
            formData =
                formData {
                    appendInput(
                        "file",
                        Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"${picName}\"",
                            )
                        },
                    ) {
                        Buffer().apply {
                            write(picBytes)
                        }
                    }
                },
        )
    }

    suspend fun randomPic(
        count: Int = 1,
        illustrators: List<String> = emptyList(),
        tags: List<String> = emptyList(),
    ): OhMyRouting.Manage.Pic.Random.Response = httpClient
        .get(
            OhMyRouting.Manage.Pic.Random(
                count = count,
                illustrators = illustrators,
                tags = tags
            ),
        ).let {
            it.body<OhMyRouting.Manage.Pic.Random.Response>()
        }

    suspend fun updatePic(pic: Pic) {
        httpClient.post(
            OhMyRouting.Manage.Pic.Update(),
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                OhMyRouting.Manage.Pic.Update.Body(
                    pic = pic,
                ),
            )
        }
    }

    suspend fun searchIllustrator(
        count: Int,
        keyword: String,
    ): OhMyRouting.Manage.Illustrator.Search.Response = httpClient
        .get(
            OhMyRouting.Manage.Illustrator.Search(
                count,
                keyword,
            ),
        ).let {
            it.body<OhMyRouting.Manage.Illustrator.Search.Response>()
        }

    suspend fun searchTag(
        count: Int,
        keyword: String,
    ): OhMyRouting.Manage.Tag.Search.Response = httpClient
        .get(
            OhMyRouting.Manage.Tag.Search(
                count,
                keyword,
            ),
        ).let {
            it.body<OhMyRouting.Manage.Tag.Search.Response>()
        }
}
