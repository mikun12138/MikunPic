package me.mikun.mikunpic

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import me.mikun.mikunpic.dto.SelectRequest
import me.mikun.mikunpic.dto.SelectResponse
import me.mikun.mikunpic.dto.UpdateRequest
import me.mikun.mikunpic.operator.fetchPic
import me.mikun.mikunpic.operator.updatePic
import me.mikun.mikunpic.operator.uploadPic
import me.mikun.mikunpic.storage.PicStorage

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
        authenticate("basic") {
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
                    val pic = fetchPic(selectRequest.filename)
                    check(pic != null)

                    call.respond(
                        SelectResponse(
                            pic
                        )
                    )
                }

                post("/update") {
                    val updateRequest = call.receive<UpdateRequest>()

                    updatePic(
                        updateRequest.picName,
                        updateRequest.illustratorName,
                        updateRequest.tagNames
                    )
                }

            }
        }
    }
}
