package me.mikun.mikunpichost.operator

import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import me.mikun.mikunpichost.storage.PicStorage

suspend fun uploadPic(
    byteChannel: ByteReadChannel,
    filename: String,
    illustratorName: String? = null,
    tags: List<String> = emptyList()
) {
    val byteArray = byteChannel.readRemaining().readByteArray()

    val hash = Digest("md5").let {
        it += byteChannel.readRemaining().readByteArray()
        it.build()
    }.toHexString()

    val illustrator = illustratorName?.let { selectOrCreateIllustrator(it) }

    val tags = tags.map { selectOrCreateTag(it) }

    createPic(
        filename,
        hash,
        illustrator,
        tags
    )

    PicStorage.upload(
        byteArray,
        filename
    )
}


