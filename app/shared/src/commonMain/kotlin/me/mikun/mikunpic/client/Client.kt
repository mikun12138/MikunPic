package me.mikun.mikunpic.client

import androidx.navigation.Navigation
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
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.Buffer
import me.mikun.mikunpic.dto.data.api.OhMyRouting

var localToken: String? = null

object Client {

    val baseUrl = "http://127.0.0.1:8080"

     val httpClient = HttpClient {
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
                "no-cache"
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
        picBytes: ByteArray
    ) {
        httpClient.submitFormWithBinaryData(
            url = "/manage/pic/upload",
            formData = formData {
                appendInput(
                    "file",
                    Headers.build {
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=\"${picName}\""
                        )
                    }
                ) {
                    Buffer().apply {
                        write(picBytes)
                    }
                }
            }
        )
    }

    suspend fun randomPic(
        count: Int = 1,
        illustrator: String? = ""
    ): OhMyRouting.Manage.Pic.Random.Response {
        return httpClient.get(
            OhMyRouting.Manage.Pic.Random(
                count = count,
                illustrator = illustrator
            )
        ).let {
            it.body<OhMyRouting.Manage.Pic.Random.Response>()
        }
    }

    suspend fun randomIllustrator(
        count: Int = 1
    ): OhMyRouting.Manage.Illustrator.Random.Response {
        return httpClient.get(
            OhMyRouting.Manage.Illustrator.Random(
                count
            )
        ).let {
            it.body<OhMyRouting.Manage.Illustrator.Random.Response>()
        }
    }


}
