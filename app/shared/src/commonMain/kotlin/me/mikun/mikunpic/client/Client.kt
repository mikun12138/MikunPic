package me.mikun.mikunpic.client

import androidx.compose.runtime.Composable
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
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.Buffer
import kotlinx.serialization.json.Json
import me.mikun.mikunpic.LocalConfig
import me.mikun.mikunpic.LocalPref
import me.mikun.mikunpic.dto.data.Illustrator
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.dto.data.api.OhMyRouting

object Client {
    lateinit var httpClient: HttpClient

    @Composable
    fun Init() {
        val server = LocalConfig.current.server
        httpClient =
            HttpClient {
                install(HttpRequestRetry) {
                    maxRetries = 3

                    exponentialDelay()

                    retryOnExceptionIf { _, _ ->
                        true
                    }
                }

                install(DefaultRequest) {
                    url(server)
                    header(
                        HttpHeaders.CacheControl,
                        "no-cache",
                    )
                }

                install(Auth) {
                    bearer {
                        loadTokens {
                            LocalPref.token?.let {
                                BearerTokens(it, null)
                            }
                        }
                    }
                }

                install(Resources)

                install(ContentNegotiation) {
                    json()
                }
            }
    }

    suspend fun uploadPic(
        picName: String,
        picBytes: ByteArray,
        illustrator: Illustrator?,
    ) {
        httpClient.post(
            OhMyRouting.Manage.Pic.Upload(),
        ) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        appendInput(
                            "file",
                            Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    """form-data; name="file"; filename="$picName"""",
                                )
                            },
                        ) {
                            Buffer().apply {
                                write(picBytes)
                            }
                        }

                        illustrator?.let {
                            append(
                                "illustrator",
                                Json.encodeToString(illustrator),
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        ContentType.Application.Json.toString(),
                                    )
                                },
                            )
                        }
                    },
                ),
            )
        }
    }

    suspend fun sync() = httpClient.post(OhMyRouting.Manage.Sync())

    suspend fun fetchPic(
        filename: String,
        thumbnail: OhMyRouting.Pic.Filename.Thumbnail = OhMyRouting.Pic.Filename.Thumbnail.Thumb,
    ) = httpClient.get(
        OhMyRouting.Pic.Filename(
            filename,
            thumbnail,
        ),
    ).let {
        it.readRawBytes()
    }

    suspend fun randomPic(
        count: Int = 1,
        illustrators: List<Illustrator> = emptyList(),
        tags: List<String> = emptyList(),
    ): OhMyRouting.Manage.Pic.Random.Response = httpClient
        .get(
            OhMyRouting.Manage.Pic.Random(
                count = count,
                illustratorIds = illustrators.mapNotNull { it.id },
                tags = tags,
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
        keyword: String = "",
        page: Int = 0,
    ): OhMyRouting.Manage.Illustrator.Search.Response = httpClient
        .get(
            OhMyRouting.Manage.Illustrator.Search(
                count = count,
                keyword = keyword,
                page = page,
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

    suspend fun selectIllustrator(id: Int): Illustrator? = httpClient
        .get(
            OhMyRouting.Manage.Illustrator.IllustratorId(id),
        ).let {
            when (it.status) {
                HttpStatusCode.OK -> it.body<OhMyRouting.Manage.Illustrator.IllustratorId.Response>().illustrator
                else -> null
            }
        }
}
