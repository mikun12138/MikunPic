package me.mikun.mikunpic.operator

import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.dto.data.Pic.Companion.toPic
import me.mikun.mikunpic.storage.PicStorage
import org.jetbrains.exposed.v1.jdbc.SizedCollection

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

    val illustrator = illustratorName?.let { Sql.selectOrCreateIllustrator(it) }

    val tags = SizedCollection(tags.map { Sql.selectOrCreateTag(it) })

    Sql.createPic(
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

suspend fun fetchPic(
    filename: String
): Pic? {
    return Sql.selectPic(filename)?.toPic()
}

suspend fun updatePic(
    filename: String,
    illustrator: String?,
    tags: List<String>,
) {
    Sql.selectPic(filename)?.let {
        Sql.updatePic(
            it,
            illustrator?.let { Sql.selectOrCreateIllustrator(it) },
            SizedCollection(tags.map { Sql.selectOrCreateTag(it) })
        )
    }
}
