package me.mikun.mikunpic.operator

import com.sun.tools.javac.code.Lint
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
import me.mikun.mikunpic.database.table.relation.Pic2IllustratorTable.illustratorId
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.storage.PicStorage
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.Random
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.SizedCollection
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

suspend fun testNow(
    illustratorIds: List<Int?>
) {
    transaction {
        PicEntity.wrapRows(
            PicTable.join(
                otherTable = Pic2IllustratorTable,
                joinType = JoinType.LEFT,
                onColumn = PicTable.id,
                otherColumn = Pic2IllustratorTable.picId,
            )
                .join(
                    otherTable = IllustratorTable,
                    joinType = JoinType.LEFT,
                    onColumn = Pic2IllustratorTable.illustratorId,
                    otherColumn = IllustratorTable.id
                )
                .selectAll()
                .where { Pic2IllustratorTable.illustratorId eq illustratorId }

        ).let {
            it.forEach {
                println("test: " + it.id)
            }
        }
    }
}

suspend fun randomPic(
    count: Int,
    illustrator: String?,
//    tags: List<String>? = null,
): List<Pic> = transaction {
    PicEntity.wrapRows(
        PicTable.join(
            otherTable = Pic2IllustratorTable,
            joinType = JoinType.LEFT,
            onColumn = PicTable.id,
            otherColumn = Pic2IllustratorTable.picId,
        )
            .selectAll()
    )

    if (illustrator == null) {
        PicEntity.find { Pic2IllustratorTable.illustratorId.isNull() }
    } else {
//        if (illustrator.isNotEmpty()) {
//            PicEntity.wrapRows(
//                PicTable.join(
//                    otherTable = IllustratorTable,
//                    joinType = JoinType.INNER,
//                    onColumn = PicTable.illustratorId,
//                    otherColumn = IllustratorTable.id,
//                )
//                    .selectAll()
//                    .where { IllustratorTable.name eq illustrator },
//            )
//        } else {
        PicEntity.all()
//        }
    }
        .limit(count)
        .orderBy(Random() to SortOrder.ASC)
        .map { it.toPic() }

//    if (illustrator == null) {
//        PicEntity.find { PicTable.illustratorId.isNull() }
//    } else {
//        if (illustrator.isNotEmpty()) {
//            PicEntity.wrapRows(
//                PicTable.join(
//                    otherTable = IllustratorTable,
//                    joinType = JoinType.INNER,
//                    onColumn = PicTable.illustratorId,
//                    otherColumn = IllustratorTable.id,
//                )
//                    .selectAll()
//                    .where { IllustratorTable.name eq illustrator },
//            )
//        } else {
//            PicEntity.all()
//        }
//    }
//        .limit(count)
//        .orderBy(Random() to SortOrder.ASC)
//        .map { it.toPic() }
}

suspend fun updatePic(
    pic: Pic,
) = transaction {
    PicEntity.findSingleByAndUpdate(PicTable.filename eq pic.filename) {
        if (!pic.illustrator.isNullOrEmpty()) {
            it.illustrator =
                IllustratorEntity.find { IllustratorTable.name eq pic.illustrator!! }.firstOrNull()
                    ?: IllustratorEntity.new {
                        this.name = pic.illustrator!!
                    }
        }
        // TODO:: tags
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
