package me.mikun.mikunpic.modules.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import me.mikun.mikunpic.storage.PicStorage

fun Route.public() {
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

    get<OhMyRouting.Pic.Filename> { req ->
        PicStorage.byName(
            req.filename,
            req.thumbnail,
        )?.let {
            call.respondBytes {
                it.readBytes()
            }
        } ?: call.respond(HttpStatusCode.NotFound)
    }
}
