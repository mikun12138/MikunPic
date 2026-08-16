package me.mikun.mikunpic.modules.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import me.mikun.mikunpic.database.StorageDB
import me.mikun.mikunpic.dto.data.Platform
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

    get<OhMyRouting.Pic.Id> { req ->
        val pic = StorageDB.byNameNoEx(req.storageLabel)?.selectPic(
            id = req.id.toInt(),
        ) ?: return@get call.respond(HttpStatusCode.NotFound)

        PicStorage.byKey(
            label = req.storageLabel,
            key = pic.storeKey,
            thumbnail = req.thumbnail,
        )?.let {
            call.respondBytes {
                it.readBytes()
            }
        } ?: call.respond(HttpStatusCode.NotFound)
    }

    get<OhMyRouting.Pic.PlatformKey> { req ->
        val platform =
            Platform.byName(req.platform) ?: return@get call.respond(HttpStatusCode.NotFound)

        for (db in StorageDB.dbs) {
            val pic = db.selectPic(
                platform = platform,
                key = req.key
            ) ?: continue

            PicStorage.byKey(
                label = db.nameNoEx,
                key = pic.storeKey,
                thumbnail = req.thumbnail,
            )?.let {
                return@get call.respondBytes {
                    it.readBytes()
                }
            }
        }

        call.respond(HttpStatusCode.NotFound)
    }
}
