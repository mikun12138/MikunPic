package me.mikun.mikunpic.operator

import io.ktor.server.routing.Route
import io.ktor.util.Digest
import me.mikun.mikunpic.database.StorageDB
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

suspend fun Route.sync() {
    TODO()
//    PicStorage.storages.forEach { storage ->
//        storage.picKeys.forEach {
//            uploadPic(
//                storageLabel = storage.label,
//                byteArray = PicStorage.byName(storage.label, it)!!.toByteReadChannel()
//                    .readRemaining()
//                    .readByteArray(),
//                filename = it,
//                uploadFile = false,
//            )
//        }
//    }
}
