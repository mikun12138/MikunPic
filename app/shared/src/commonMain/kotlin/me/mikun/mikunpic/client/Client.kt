package me.mikun.mikunpic.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

val httpClient = HttpClient() {
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
}

