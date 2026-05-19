package me.mikun.mikunpichost

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.utils.io.ByteReadChannel
import me.mikun.me.mikun.mikunpichost.dto.data.Pic
import me.mikun.mikunpichost.database.PicEntity
import me.mikun.mikunpichost.dto.SelectRequest
import me.mikun.mikunpichost.dto.SelectResponse
import me.mikun.mikunpichost.dto.UpdateRequest
import me.mikun.mikunpichost.operator.selectPic
import me.mikun.mikunpichost.operator.updatePic
import me.mikun.mikunpichost.operator.uploadPic
import me.mikun.mikunpichost.storage.PicStorage

@Suppress("ktlint:standard:kdoc")
fun Application.configureRouting() {
    routing {
        /**
         * @description get random image
         */
        get("/random") {
            PicStorage.random()?.let {
                call.respondBytes {
                    it.readBytes()
                }
            } ?: call.respond(HttpStatusCode.NotFound)
        }

        route("/manage") {
            post("/upload") {
                val multipart = call.receiveMultipart()

                var fileDescription: String? = null
                var fileChannel: ByteReadChannel?
                var filename: String?

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileChannel = part.provider()
                            filename = part.originalFileName

                            uploadPic(
                                fileChannel,
                                filename!!
                            )

                        }

                        is PartData.FormItem -> {
                            fileDescription = part.value

                        }

                        else -> part.dispose()
                    }
                }


                call.respond(
                    HttpStatusCode.Created,
                    mapOf("message" to "$fileDescription Upload Success.")
                )
            }

            post("/select") {
                val selectRequest = call.receive<SelectRequest>()
                val picEntity = selectPic(selectRequest.filename)
                check(picEntity != null)

                call.respond(
                    SelectResponse(
                        Pic.fromPicEntity(
                            picEntity
                        )
                    )
                )
            }

            post("/update") {
                val updateRequest = call.receive<UpdateRequest>()

                val picEntity = selectPic(updateRequest.picName)
                check(picEntity != null)
                updatePic(
                    picEntity,
                    updateRequest.illustratorName,
                    updateRequest.tagNames
                )
            }

            post("/delete") {
                val
            }

        }
    }
}
