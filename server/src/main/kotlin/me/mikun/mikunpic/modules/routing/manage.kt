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
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.launch
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import me.mikun.mikunpic.operator.sync
import me.mikun.mikunpic.operator.backup
import me.mikun.mikunpic.operator.createIllustrator
import me.mikun.mikunpic.operator.createTag
import me.mikun.mikunpic.operator.randomIllustrator
import me.mikun.mikunpic.operator.randomPic
import me.mikun.mikunpic.operator.searchIllustrator
import me.mikun.mikunpic.operator.searchTag
import me.mikun.mikunpic.operator.selectIllustrator
import me.mikun.mikunpic.operator.updatePic
import me.mikun.mikunpic.operator.uploadPic
import me.mikun.mikunpic.utils.mapToNullable

fun Route.manage() {

    fun pic() {
        post<OhMyRouting.Manage.Pic.Upload> {
            val multipart = call.receiveMultipart()

            var fileDescription: String? = null
            var fileChannel: ByteReadChannel?
            var filename: String? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileChannel = part.provider()
                        filename = part.originalFileName

                        uploadPic(
                            fileChannel,
                            filename!!,
                        )
                    }

                    is PartData.FormItem -> {
                        when (part.name) {
                            "description" -> fileDescription = part.value
                        }
                    }

                    else -> part.dispose()
                }
            }

            fileDescription = fileDescription ?: filename

            call.respond(
                HttpStatusCode.Created,
                mapOf("message" to "$fileDescription Upload Success."),
            )
        }

        get<OhMyRouting.Manage.Pic.Random> { req ->
            randomPic(
                req.count,
                req.illustrators.mapToNullable().toSet(),
                req.tags.mapToNullable().toSet(),
            ).let {
                call.respond(
                    OhMyRouting.Manage.Pic.Random.Response(
                        it,
                    )
                )
            }
        }

        post<OhMyRouting.Manage.Pic.Update> {
            val receive = call.receive<OhMyRouting.Manage.Pic.Update.Body>()

            updatePic(receive.pic)

            call.respond(HttpStatusCode.Created)
        }
    }

    fun illustrator() {
        post<OhMyRouting.Manage.Illustrator.Create> {
            val receive = call.receive<OhMyRouting.Manage.Illustrator.Create.Body>()

            createIllustrator(receive.name)
        }

        get<OhMyRouting.Manage.Illustrator.Random> { req ->
            randomIllustrator(
                req.count
            ).let {
                call.respond(
                    OhMyRouting.Manage.Illustrator.Random.Response(
                        it,
                    ),
                )
            }
        }

        get<OhMyRouting.Manage.Illustrator.Search> { req ->
            searchIllustrator(
                count = req.count,
                keyword = req.keyword,
                page = req.page
            ).let {
                call.respond(
                    OhMyRouting.Manage.Illustrator.Search.Response(
                        it,
                    ),
                )
            }
        }

        get<OhMyRouting.Manage.Illustrator.IllustratorId> { req ->
            selectIllustrator(
                req.illustratorId
            )?.let {
                call.respond(
                    OhMyRouting.Manage.Illustrator.IllustratorId.Response(
                        it
                    )
                )
            } ?: call.respond(HttpStatusCode.NotFound)
        }
    }

    fun tag() {
        post<OhMyRouting.Manage.Tag.Create> {
            val receive = call.receive<OhMyRouting.Manage.Tag.Create.Body>()

            runCatching {
                createTag(receive.name)
            }

            call.respond(HttpStatusCode.Created)
        }

        get<OhMyRouting.Manage.Tag.Search> { req ->
            searchTag(
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

    pic()
    illustrator()
    tag()

    post<OhMyRouting.Manage.Backup> {
        backup()
    }

    post<OhMyRouting.Manage.Sync> {
        application.launch {
            sync()
        }
        call.respond(HttpStatusCode.OK)
    }


}