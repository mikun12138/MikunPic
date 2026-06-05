package me.mikun.mikunpic.operator

import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import me.mikun.mikunpic.database.IllustratorEntity
import me.mikun.mikunpic.database.IllustratorTable
import me.mikun.mikunpic.database.IllustratorTable.innerJoin
import me.mikun.mikunpic.database.PicEntity
import me.mikun.mikunpic.database.PicTable
import me.mikun.mikunpic.database.TagEntity
import me.mikun.mikunpic.database.TagTable
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.storage.PicStorage
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.Random
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

suspend fun uploadPic(
    byteChannel: ByteReadChannel,
    filename: String,
    illustratorName: String? = null,
    tags: List<String> = emptyList(),
) {
    val byteArray = byteChannel.readRemaining().readByteArray()

    val hash = Digest("md5").let {
        it += byteArray
        it.build()
    }.toHexString()

    PicStorage.upload(
        byteArray,
        filename
    )

    transaction {
        val illustrator = illustratorName?.let {
            IllustratorEntity.find { IllustratorTable.name eq it }.firstOrNull()
                ?: IllustratorEntity.new {
                    this.name = it
                }
        }

        val tags = SizedCollection(tags.map {
            TagEntity.find { TagTable.name eq it }.firstOrNull() ?: TagEntity.new {
                this.name = it
            }
        })

        PicEntity.new {
            this.filename = filename
            this.hash = hash
            this.illustrator = illustrator
            this.tags = tags
        }

    }
}

suspend fun randomPic(
    count: Int,
    illustrator: String?,
//    tags: List<String>? = null,
): List<Pic> {
    return transaction {
        if (illustrator == null) {
            PicEntity.find { PicTable.illustratorId.isNull() }
        } else {
            if (illustrator.isNotEmpty()) {
                PicEntity.wrapRows(
                    PicTable.join(
                        otherTable = IllustratorTable,
                        joinType = JoinType.INNER,
                        onColumn = PicTable.illustratorId,
                        otherColumn = IllustratorTable.id
                    )
                        .selectAll()
                        .where { IllustratorTable.name eq illustrator }
                )
            } else {
                PicEntity.all()
            }
        }
            .limit(count)
            .orderBy(Random() to SortOrder.ASC)
            .map { it.toPic() }
    }
}


suspend fun updatePic(
    filename: String,
    illustrator: String?,
    tags: List<String>,
) {
    transaction {
        PicEntity.find(PicTable.filename eq filename).firstOrNull()?.let {
            it.illustrator = illustrator?.let {
                IllustratorEntity.find { IllustratorTable.name eq it }.firstOrNull()
                    ?: IllustratorEntity.new {
                        this.name = it
                    }
            }
            it.tags = SizedCollection(tags.map {
                TagEntity.find { TagTable.name eq it }.firstOrNull() ?: TagEntity.new {
                    this.name = it
                }
            })
        }
    }
}


suspend fun createIllustrator(
    illustrator: String?,
) {
    transaction {
        illustrator?.let {
            IllustratorEntity.new {
                this.name = illustrator
            }
        }
    }
}

suspend fun randomIllustrator(
    count: Int,
): List<String> {
    return transaction {

        IllustratorEntity.find { IllustratorTable.name.isNotNull() }
            .orderBy(Random() to SortOrder.ASC)
            .limit(count)
            .map { it.name }
    }
}