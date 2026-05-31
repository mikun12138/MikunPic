package me.mikun.mikunpic.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

var localToken: String? = null

val httpClient = HttpClient {
    install(HttpRequestRetry) {
        maxRetries = 3

        exponentialDelay()

        retryOnExceptionIf { _, _ ->
            true
        }
    }

    install(DefaultRequest) {
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
}

