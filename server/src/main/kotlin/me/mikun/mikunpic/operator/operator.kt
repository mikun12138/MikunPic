package me.mikun.mikunpic.operator

import io.ktor.util.Digest
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import me.mikun.mikunpic.database.IllustratorEntity
import me.mikun.mikunpic.database.PicEntity
import me.mikun.mikunpic.database.TagEntity
import me.mikun.mikunpic.database.table.IllustratorTable
import me.mikun.mikunpic.database.table.PicTable
import me.mikun.mikunpic.database.table.TagTable
import me.mikun.mikunpic.database.table.relation.Pic2IllustratorTable
import me.mikun.mikunpic.database.table.relation.Pic2TagsTable
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.storage.PicStorage
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.Random
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.select
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
        filename,
    )

    transaction {
        val illustrator = illustratorName?.let {
            IllustratorEntity.find { IllustratorTable.name eq it }.firstOrNull()
                ?: IllustratorEntity.new {
                    this.name = it
                }
        }

        val tags = SizedCollection(
            tags.map {
                TagEntity.find { TagTable.name eq it }.firstOrNull() ?: TagEntity.new {
                    this.name = it
                }
            },
        )

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
    illustrators: Set<String?>,
    tags: Set<String?> = setOf(),
): List<Pic> = transaction {
    PicEntity.wrapRows(
        PicTable.join(
            otherTable = Pic2IllustratorTable,
            joinType = JoinType.LEFT,
            onColumn = PicTable.id,
            otherColumn = Pic2IllustratorTable.picId
        ).join(
            otherTable = IllustratorTable,
            joinType = JoinType.LEFT,
            onColumn = Pic2IllustratorTable.illustratorId,
            otherColumn = IllustratorTable.id
        ).join( // TODO:: make it separate
            otherTable = Pic2TagsTable,
            joinType = JoinType.LEFT,
            onColumn = PicTable.id,
            otherColumn = Pic2TagsTable.picId
        ).join(
            otherTable = TagTable,
            joinType = JoinType.LEFT,
            onColumn = Pic2TagsTable.tagId,
            otherColumn = TagTable.id
        )
            .select(PicTable.columns)
            .apply { // with effect so
                if (illustrators.isNotEmpty()) {
                    andWhere {
                        var op: Op<Boolean> =
                            (IllustratorTable.name inList illustrators.filterNotNull())

                        if (illustrators.contains(null))
                            op = op or IllustratorTable.name.isNull()

                        op
                    }
                }

                if (tags.isNotEmpty()) {
                    andWhere {
                        var op: Op<Boolean> =
                            TagTable.name inList tags.filterNotNull()

                        if (tags.contains(null))
                            op = op or TagTable.name.isNull()

                        op
                    }
                }
            }
            .withDistinct()

    )
        .limit(count)
        .orderBy(Random() to SortOrder.ASC)
        .map { it.toPic() }
}

suspend fun updatePic(
    pic: Pic,
) = transaction {
    PicEntity.findSingleByAndUpdate(PicTable.filename eq pic.filename) { picEntity ->
        if (!pic.illustrator.isNullOrEmpty()) {
            picEntity.illustrator =
                IllustratorEntity.find { IllustratorTable.name eq pic.illustrator!! }.firstOrNull()
                    ?: IllustratorEntity.new {
                        this.name = pic.illustrator!!
                    }
        }

        println(pic.tags)

        val tagsInTable = TagEntity.find { TagTable.name inList pic.tags }

        val newTags = (pic.tags - tagsInTable.map { it.name }.toSet()).map {
            TagEntity.new {
                this.name = it
            }
        }

        tagsInTable.forEach { println(it.name) }
        newTags.forEach { println(it.name) }

        picEntity.tags = SizedCollection(tagsInTable + newTags)

    }
}

suspend fun searchIllustrator(
    count: Int,
    keyword: String? = null,
): List<String> = transaction {
    if (!keyword.isNullOrEmpty()) {
        IllustratorEntity.find { IllustratorTable.name like "%$keyword%" }
            .limit(count)
            .map { it.name }
    } else {
        emptyList()
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
): List<String> = transaction {
    IllustratorEntity.find { IllustratorTable.name.isNotNull() }
        .orderBy(Random() to SortOrder.ASC)
        .limit(count)
        .map { it.name }
}


suspend fun createTag(
    tag: String?,
) {
    transaction {
        tag?.let {
            TagEntity.new {
                this.name = tag
            }
        }
    }
}

suspend fun searchTag(
    count: Int,
    keyword: String? = null,
): List<String> = transaction {
    if (!keyword.isNullOrEmpty()) {
        TagEntity.find { TagTable.name like "%$keyword%" }
            .limit(count)
            .map { it.name }
    } else {
        emptyList()
    }
}


