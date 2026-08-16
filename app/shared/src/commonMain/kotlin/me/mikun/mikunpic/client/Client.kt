package me.mikun.mikunpic.client

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.network.ktor3.KtorNetworkFetcherFactory
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
import io.ktor.client.statement.HttpResponse
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
import me.mikun.mikunpic.dto.data.PicCreate
import me.mikun.mikunpic.dto.data.PicUpdate
import me.mikun.mikunpic.dto.data.api.OhMyRouting

object Client {
    lateinit var httpClient: HttpClient

    @OptIn(ExperimentalCoilApi::class)
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

        SingletonImageLoader.setSafe {
            ImageLoader.Builder(it)
                .components {
                    add(
                        KtorNetworkFetcherFactory(
                            httpClient,
                        ),
                    )
                }
                .build()
        }
    }

    @Suppress("ktlint:standard:function-naming")
    private suspend fun HttpResponse.`get bytes`(): ByteArray? = when (this.status) {
        HttpStatusCode.OK -> this.readRawBytes()
        else -> null
    }

    @Suppress("ktlint:standard:function-naming")
    private suspend inline fun <reified T> HttpResponse.`get any`(): T? = when (this.status) {
        HttpStatusCode.OK -> this.body<T>()
        else -> null
    }

    suspend fun fetchPic(
        id: String,
        thumbnail: OhMyRouting.Pic.Thumbnail = OhMyRouting.Pic.Thumbnail.Thumb,
        storageLabel: String,
    ) = httpClient
        .get(
            OhMyRouting.Pic.Id(
                id = id,
                thumbnail = thumbnail,
                storageLabel = storageLabel
            ),
        ).`get bytes`()

    suspend fun uploadPic(
        storageLabel: String,
        picBytes: ByteArray,
        pic: PicCreate
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
                                    """form-data; name="file"; filename="${pic.filename}"""",
                                )
                            },
                        ) {
                            Buffer().apply {
                                write(picBytes)
                            }
                        }

                        append(
                            "storage_label",
                            storageLabel,
                        )

                        append(
                            "pic",
                            Json.encodeToString(
                                pic
                            )
                        )
                    },
                ),
            )
        }
    }

    suspend fun randomPic(
        count: Int = 1,
        illustrators: List<Illustrator> = emptyList(),
        tags: List<String> = emptyList(),
        storageLabels: List<String> = emptyList(),
    ) = httpClient
        .post(
            OhMyRouting.Manage.Pic.Random()
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                OhMyRouting.Manage.Pic.Random.Body(
                    count = count,
                    illustratorIds = illustrators.mapNotNull { it.id },
                    tags = tags,
                    storageLabels = storageLabels
                )
            )
        }
        .`get any`<OhMyRouting.Manage.Pic.Random.Response>()

    suspend fun updatePic(
        storageLabel: String,
        pic: PicUpdate,
    ) {
        httpClient.post(
            OhMyRouting.Manage.Pic.Update(),
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                OhMyRouting.Manage.Pic.Update.Body(
                    storageLabel = storageLabel,
                    pic = pic,
                ),
            )
        }
    }

    suspend fun searchIllustrator(
        count: Int,
        keyword: String = "",
        page: Int = 0,
    ) = httpClient
        .get(
            OhMyRouting.Manage.Illustrator.Search(
                count = count,
                keyword = keyword,
                page = page,
            ),
        ).`get any`<OhMyRouting.Manage.Illustrator.Search.Response>()

    suspend fun createTag(
        tagName: String,
    ) {
        httpClient
            .post(
                OhMyRouting.Manage.Tag.Create()
            ) {
                contentType(ContentType.Application.Json)
                setBody(
                    OhMyRouting.Manage.Tag.Create.Body(
                        name = tagName
                    )
                )
            }
    }

    suspend fun searchTag(
        count: Int,
        keyword: String = "",
        page: Int = 0
    ) = httpClient
        .get(
            OhMyRouting.Manage.Tag.Search(
                count = count,
                keyword = keyword,
                page = page
            ),
        ).`get any`<OhMyRouting.Manage.Tag.Search.Response>()

    suspend fun deleteTag(
        tagName: String,
    ) {
        httpClient
            .post(
                OhMyRouting.Manage.Tag.Delete()
            ) {
                contentType(ContentType.Application.Json)
                setBody(
                    OhMyRouting.Manage.Tag.Delete.Body(
                        name = tagName
                    )
                )
            }
    }

    suspend fun fetchStorages() =
        httpClient
            .get(
                OhMyRouting.Manage.Storages()
            ).`get any`<OhMyRouting.Manage.Storages.Response>()

    suspend fun sync() = httpClient
        .post(
            OhMyRouting.Manage.Sync(),
        )

}
