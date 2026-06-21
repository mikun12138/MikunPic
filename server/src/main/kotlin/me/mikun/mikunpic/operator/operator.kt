package me.mikun.mikunpic.operator

import io.ktor.server.application.log
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.util.Digest
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import me.mikun.mikunpic.database.IllustratorEntity
import me.mikun.mikunpic.database.PicEntity
import me.mikun.mikunpic.database.PlatformKeyEntity
import me.mikun.mikunpic.database.TagEntity
import me.mikun.mikunpic.database.table.IllustratorTable
import me.mikun.mikunpic.database.table.PicTable
import me.mikun.mikunpic.database.table.PlatformKeyTable
import me.mikun.mikunpic.database.table.TagTable
import me.mikun.mikunpic.database.table.relation.Illustrator2PlatformKeysTable
import me.mikun.mikunpic.database.table.relation.Pic2IllustratorTable
import me.mikun.mikunpic.database.table.relation.Pic2TagsTable
import me.mikun.mikunpic.dto.data.Illustrator
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import me.mikun.mikunpic.modules.db
import me.mikun.mikunpic.storage.PicStorage
import me.mikun.mikunpic.storage.PicStorageCos
import me.mikun.mikunpic.storage.PicStorageLocal
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.Random
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.orWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.sql.Connection

suspend fun Route.uploadPic(
    byteArray: ByteArray,
    filename: String,
    illustrator: Illustrator? = null,
    tags: List<String> = emptyList(),
    uploadFile: Boolean = true,
) {

    try {
        val hash = Digest("md5").let {
            it += byteArray
            it.build()
        }.toHexString()

        if (uploadFile) {
            PicStorage.upload(
                byteArray,
                filename,
            )
        }

        application.log.error(illustrator.toString())

        transaction {
            val illustratorEntity: IllustratorEntity? = illustrator?.let {
                it.id?.let { id ->
                    IllustratorEntity.findById(id)
                } ?: it.platformKeyMap.takeIf { it.isNotEmpty() }?.let { platformKeyMap ->
                    IllustratorEntity.wrapRows(
                        IllustratorTable.join(
                            otherTable = Illustrator2PlatformKeysTable,
                            joinType = JoinType.LEFT,
                            onColumn = IllustratorTable.id,
                            otherColumn = Illustrator2PlatformKeysTable.illustrator
                        ).join(
                            otherTable = PlatformKeyTable,
                            joinType = JoinType.LEFT,
                            onColumn = Illustrator2PlatformKeysTable.platformkey,
                            otherColumn = PlatformKeyTable.id
                        ).select(IllustratorTable.columns)
                            .apply {
                                platformKeyMap.forEach { (platform, key) ->
                                    orWhere {
                                        (PlatformKeyTable.platform eq platform
                                                ) and (PlatformKeyTable.key eq key)
                                    }
                                }
                            }
                    ).firstOrNull()
                } ?: illustrator.name?.let { name ->
                    IllustratorEntity.new {
                        this.name = name
                        this.platformKeys =
                            SizedCollection(
                                illustrator.platformKeyMap.map { (platform, key) ->
                                    PlatformKeyEntity.new {
                                        this.platform = platform
                                        this.key = key
                                    }
                                }
                            )
                    }
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
                this.illustrator = illustratorEntity
                this.tags = tags
            }
        }

        application.log.info("upload pic $filename")
    } catch (e: Exception) {
        application.log.error("failed to upload pic $filename : $e")
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
    page: Int = 0,
): List<Illustrator> = transaction {
    val offset = (page.coerceAtLeast(0) * count).toLong()
    if (!keyword.isNullOrEmpty()) {
        IllustratorEntity.find { IllustratorTable.name like "%$keyword%" }
            .orderBy(IllustratorTable.id to SortOrder.ASC)
            .limit(count)
            .offset(offset)
            .map {
                Illustrator(
                    id = it.id.value,
                    name = it.name,
                    // TODO:: return platform key
                    emptyMap()
                )
            }
    } else {
        IllustratorEntity
            .all()
            .orderBy(IllustratorTable.id to SortOrder.ASC)
            .limit(count)
            .offset(offset)
            .map {
                Illustrator(
                    id = it.id.value,
                    name = it.name,
                    // TODO:: return platform key
                    emptyMap()
                )
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
): List<String> = transaction {
    IllustratorEntity.find { IllustratorTable.name.isNotNull() }
        .orderBy(Random() to SortOrder.ASC)
        .limit(count)
        .map { it.name }
}

suspend fun selectIllustrator(
    illustratorId: Int,
): Illustrator? {
    return transaction {
        IllustratorEntity.findById(illustratorId)?.let {
            Illustrator(
                id = illustratorId,
                name = it.name,
                // TODO:: return platform key
                emptyMap()
            )
        }
    }
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

suspend fun backup() {
    (db.connector().connection as Connection).use { connection ->
        connection.createStatement().use { statement ->
            val sql = "VACUUM INTO './data/databases/pic.db.bak'"
            statement.executeUpdate(sql)
        }
    }
}

suspend fun Route.sync() {
    PicStorage.picKeys.forEach {
        uploadPic(
            byteArray = PicStorage.byName(it)!!.toByteReadChannel().readRemaining().readByteArray(),
            filename = it,
            uploadFile = false
        )
    }
}

