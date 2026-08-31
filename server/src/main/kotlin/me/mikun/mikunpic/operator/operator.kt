package me.mikun.mikunpic.operator

import io.ktor.server.routing.Route
import io.ktor.util.Digest
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import me.mikun.mikunpic.database.StorageDB
import me.mikun.mikunpic.dto.awesome.PicPathResolver
import me.mikun.mikunpic.dto.data.PicCreate
import me.mikun.mikunpic.storage.PicStorage

suspend fun Route.uploadPic(
    storageLabel: String,
    byteArray: ByteArray,
    pic: PicCreate,
    uploadFile: Boolean = true,
) {
    // TODO:: hash, no
    val hash = Digest("md5").let {
        it += byteArray
        it.build()
    }.toHexString()

    StorageDB.byNameNoEx(storageLabel)?.apply {
        selectPic(
            hash = hash
        )?.run {
            return
        }

        if (uploadFile) {
            PicStorage.upload(
                storageLabel,
                byteArray,
                pic.storeKey,
            )
        }

        createPic(
            pic = pic,
            hash = hash
        )
    }
}

suspend fun Route.sync(
    storageLabel: String,
    syncRuleText: String,
) {
    val picPathResolver = PicPathResolver(
        syncRuleText
    )

    PicStorage.storages.find { it.label == storageLabel }?.let { storage ->
        storage.picKeys.forEach { picKey ->
            val picCreate = picPathResolver.resolve(
                path = picKey.split("/"),
                filename = { picKey }
            ) ?: return

            uploadPic(
                storageLabel = storage.label,
                byteArray = storage.byKey(
                    key = picKey
                )!!.toByteReadChannel()
                    .readRemaining()
                    .readByteArray(),
                pic = picCreate,
                uploadFile = false,
            )
        }
    }
}
