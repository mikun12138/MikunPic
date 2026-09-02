package me.mikun.mikunpic.api

import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import me.mikun.mikunpic.api.ApiTest.ohMyTest
import me.mikun.mikunpic.dto.data.Storage
import me.mikun.mikunpic.dto.data.api.OhMyRouting.Manage
import kotlin.test.Test

class Storage {

    @Test
    fun add() = ohMyTest {
        client.post(
            Manage.Storage.Add(),
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                Manage.Storage.Add.Body(
                    Storage.Cos(
                        label = "test0",
                        secretId = "4",
                        secretKey = "3",
                        bucketName = "2",
                        region = "1",
                    ),
                ),
            )
        }
    }

    @Test
    fun edit() = ohMyTest {
        client.post(
            Manage.Storage.Edit(),
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                Manage.Storage.Edit.Body(
                    Storage.Cos(
                        label = "test0",
                        secretId = "1",
                        secretKey = "2",
                        bucketName = "3",
                        region = "4",
                    ),
                ),
            )
        }
    }

    @Test
    fun delete() = ohMyTest {
        client.post(
            Manage.Storage.Delete(),
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                Manage.Storage.Delete.Body(
                    storageLabel = "test0",
                ),
            )
        }
    }
}
