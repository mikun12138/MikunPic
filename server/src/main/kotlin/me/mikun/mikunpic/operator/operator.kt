package me.mikun.mikunpic.operator

import io.ktor.server.routing.Route
import io.ktor.util.Digest
import me.mikun.mikunpic.LocalMikunPicConfig
import me.mikun.mikunpic.database.StorageDB
import me.mikun.mikunpic.dto.awesome.PicPathResolver
import me.mikun.mikunpic.dto.data.MikunPicConfig
import me.mikun.mikunpic.dto.data.PicCreate
import me.mikun.mikunpic.storage.PicStorage

suspend fun Route.uploadPic(
    storageLabel: String,
    byteArray: ByteArray,
    pic: PicCreate,
) {
    val hash = Digest("md5").let {
        it += byteArray
        it.build()
    }.toHexString()

    StorageDB.find(storageLabel)?.apply {
        selectPic(
            hash = hash,
        )?.run {
            return
        }

        createPic(
            pic = pic,
            hash = hash,
        )

        PicStorage.upload(
            storageLabel,
            byteArray,
            pic.storeKey,
        )
    }
}

suspend fun Route.sync(
    storageLabel: String,
    syncRuleText: String,
) {
    LocalMikunPicConfig = LocalMikunPicConfig.copy(
        storages = LocalMikunPicConfig.storages.map {
            if (it.label == storageLabel) {
                when (it) {
                    is MikunPicConfig.Storage.Local -> it.copy(pathRule = syncRuleText)
                    is MikunPicConfig.Storage.Cos -> it.copy(pathRule = syncRuleText)
                }
            } else {
                it
            }
        },
    )

    PicStorage.storages.find { it.label == storageLabel }?.let { storage ->
        val picPathResolver = PicPathResolver(
            syncRuleText,
        )
        storage.picKeys.forEach loop@ { picKey ->
            val picCreate = picPathResolver.resolve(
                path = picKey.split("/"),
                filename = { it },
            ) ?: return@loop

            val hash = storage.hash(picKey) ?: error("no hash")

            StorageDB.find(storageLabel)?.apply {
                selectPic(
                    hash = hash,
                )?.run {
                    return@loop
                }

                createPic(
                    pic = picCreate,
                    hash = hash,
                )
            }
            println("sync::$hash:$picKey")
        }
    }
}
