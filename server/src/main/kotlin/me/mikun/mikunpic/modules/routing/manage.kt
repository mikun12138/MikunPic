package me.mikun.mikunpic.modules.routing

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.launch
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import me.mikun.mikunpic.database.StorageDB
import me.mikun.mikunpic.dto.data.Illustrator
import me.mikun.mikunpic.dto.data.Storage
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import me.mikun.mikunpic.operator.backup
import me.mikun.mikunpic.operator.createIllustrator
import me.mikun.mikunpic.operator.randomPic
import me.mikun.mikunpic.operator.searchIllustrator
import me.mikun.mikunpic.operator.searchTag
import me.mikun.mikunpic.operator.selectIllustrator
import me.mikun.mikunpic.operator.sync
import me.mikun.mikunpic.operator.updatePic
import me.mikun.mikunpic.operator.uploadPic
import me.mikun.mikunpic.storage.PicStorage
import me.mikun.mikunpic.utils.mapToNullable

fun Route.manage() {
    fun storage() {
        get<OhMyRouting.Manage.Storages> {
            call.respond(
                OhMyRouting.Manage.Storages.Response(
                    PicStorage.storages.map {
                        Storage(
                            it.label
                        )
                    }
                )
            )
        }
    }

    fun pic() {
        post<OhMyRouting.Manage.Pic.Upload> {
            val multipart = call.receiveMultipart()

            var byteArray: ByteArray? = null
            var filename: String? = null
            var illustrator: Illustrator? = null
            var storageLabel: String? = null
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        byteArray = part.provider().readRemaining().readByteArray()
                        filename = part.originalFileName
                    }

                    is PartData.FormItem -> {
                        when (part.name) {
                            "illustrator" -> {
                                illustrator = Json.decodeFromString(part.value)
                            }

                            "storage_label" -> {
                                storageLabel = part.value
                            }
                        }
                    }

                    else -> part.dispose()
                }
            }

            if (byteArray == null || filename == null || storageLabel == null) {
                call.respond(
                    HttpStatusCode.BadGateway,
                )
                return@post
            }

            uploadPic(
                storageLabel,
                byteArray,
                filename!!,
                illustrator,
            )

            call.respond(
                HttpStatusCode.Created,
            )
        }

        post<OhMyRouting.Manage.Pic.Random> {
            val receive = call.receive<OhMyRouting.Manage.Pic.Random.Body>()

            randomPic(
                receive.storageLabels.toSet(),
                receive.count,
                receive.illustratorIds.toSet(),
                receive.tags.mapToNullable().toSet(),
            ).let {
                call.respond(
                    OhMyRouting.Manage.Pic.Random.Response(
                        it.first,
                        it.second
                    ),
                )
            }
        }

        post<OhMyRouting.Manage.Pic.Update> {
            val receive = call.receive<OhMyRouting.Manage.Pic.Update.Body>()

            updatePic(
                label = receive.storageLabel,
                receive.pic
            )

            call.respond(HttpStatusCode.Created)
        }
    }

    fun illustrator() {
        post<OhMyRouting.Manage.Illustrator.Create> {
            val receive = call.receive<OhMyRouting.Manage.Illustrator.Create.Body>()

            StorageDB.byNameNoEx(receive.storageLabel)?.apply {
                createIllustrator(
                    receive.name
                )
            }
        }

        post<OhMyRouting.Manage.Illustrator.Search> { req ->
            searchIllustrator(
                count = req.count,
                keyword = req.keyword,
                page = req.page,
            ).let {
                call.respond(
                    OhMyRouting.Manage.Illustrator.Search.Response(
                        it,
                    ),
                )
            }
        }
    }

    fun tag() {
        post<OhMyRouting.Manage.Tag.Create> {
            val receive = call.receive<OhMyRouting.Manage.Tag.Create.Body>()

            StorageDB.byNameNoEx(receive.storageLabel)?.apply {
                createTag(
                    receive.name
                )
            }

            call.respond(HttpStatusCode.Created)
        }

        post<OhMyRouting.Manage.Tag.Delete> {
            val receive = call.receive<OhMyRouting.Manage.Tag.Delete.Body>()
            StorageDB.byNameNoEx(receive.storageLabel)?.apply {
                deleteTag(
                    receive.name
                )
            }

            call.respond(HttpStatusCode.Accepted)
        }

        get<OhMyRouting.Manage.Tag.Search> { req ->
            StorageDB.searchTag(
                count = req.count,
                keyword = req.keyword,
            ).let {
                call.respond(
                    OhMyRouting.Manage.Tag.Search.Response(
                        it,
                    ),
                )
            }
        }
    }

    storage()

    pic()
    illustrator()
    tag()

    post<OhMyRouting.Manage.Backup> {
        StorageDB.backup()
        call.respond(HttpStatusCode.OK)
    }

    post<OhMyRouting.Manage.Sync> {
        sync()
        call.respond(HttpStatusCode.OK)
    }
}
