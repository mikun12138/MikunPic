package me.mikun.mikunpic

import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.contentType
import io.ktor.server.routing.getAllRoutes
import io.ktor.server.routing.routing
import io.ktor.utils.io.ByteReadChannel
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import me.mikun.mikunpic.operator.createIllustrator
import me.mikun.mikunpic.operator.createTag
import me.mikun.mikunpic.operator.randomIllustrator
import me.mikun.mikunpic.operator.randomPic
import me.mikun.mikunpic.operator.searchIllustrator
import me.mikun.mikunpic.operator.searchTag
import me.mikun.mikunpic.operator.updatePic
import me.mikun.mikunpic.operator.uploadPic
import me.mikun.mikunpic.storage.PicStorage
import me.mikun.mikunpic.utils.mapToNullable

@Suppress("ktlint:standard:kdoc")
fun Application.configureRouting() {
    routing {
        public()

//        authenticate("bearer") {
//        get("/auth") { }
        manage()
//        }
    }.let {
        println(it.getAllRoutes())
    }
}

private fun Route.public() {
    /**
     * @description get random image
     */
    get<OhMyRouting.Random> {
        PicStorage.random()?.let {
            call.respondBytes {
                it.readBytes()
            }
        } ?: call.respond(HttpStatusCode.NotFound)
    }

    get<OhMyRouting.Pic.Filename> {
        PicStorage.byName(it.filename)?.let {
            call.respondBytes {
                it.readBytes()
            }
        } ?: call.respond(HttpStatusCode.NotFound)
    }
}

private fun Route.manage() {
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

    post<OhMyRouting.Manage.Pic.Update> {
        val receive = call.receive<OhMyRouting.Manage.Pic.Update.Body>()
        updatePic(receive.pic)

        call.respond(HttpStatusCode.Created)
    }

    get<OhMyRouting.Manage.Illustrator.Random> { req ->

        randomIllustrator(req.count).let {
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
        ).let {
            call.respond(
                OhMyRouting.Manage.Illustrator.Search.Response(
                    it,
                ),
            )
        }
    }

    post<OhMyRouting.Manage.Illustrator.Create> {
        val receive = call.receive<OhMyRouting.Manage.Illustrator.Create.Body>()

        createIllustrator(receive.name)
    }

    post<OhMyRouting.Manage.Tag.Create> {
        val receive = call.receive<OhMyRouting.Manage.Tag.Create.Body>()

        createTag(receive.name)
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
