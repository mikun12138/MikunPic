package me.mikun.mikunpic.database

import me.mikun.mikunpic.database.table.IllustratorTable
import me.mikun.mikunpic.database.table.PicTable
import me.mikun.mikunpic.database.table.PlatformKeyTable
import me.mikun.mikunpic.database.table.TagTable
import me.mikun.mikunpic.database.table.relation.Illustrator2PlatformKeysTable
import me.mikun.mikunpic.database.table.relation.Pic2IllustratorTable
import me.mikun.mikunpic.database.table.relation.Pic2TagsTable
import me.mikun.mikunpic.dto.data.Illustrator
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.dto.data.Platform
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.Random
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.notInList
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.name
import org.jetbrains.exposed.v1.jdbc.orWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.upsert
import java.sql.Connection
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.let
import kotlin.use

class StorageDB(
    val db: Database,
) {
    init {
        transaction(db) {
            SchemaUtils.create(
                PicTable,
                Pic2IllustratorTable,
                Pic2TagsTable,
            )
        }
    }

    val nameNoEx
        get() = db.name.removeSuffix(".db")

    val countPic
        get() = transaction(db) {
            PicEntity.count()
        }

    suspend fun createPic(
        pic: Pic,
        hash: String,
    ) {
        transaction(db) {
            var newIllustratorId: Int? = null
            val newTagIds = mutableListOf<Int>()
            transaction(MetadataDB.db) {
                pic.illustrator?.let { illustrator ->
                    val illustratorQuery = PlatformKeyTable.join(
                        otherTable = Illustrator2PlatformKeysTable,
                        joinType = JoinType.LEFT,
                        onColumn = PlatformKeyTable.id,
                        otherColumn = Illustrator2PlatformKeysTable.platformkey
                    ).join(
                        otherTable = IllustratorTable,
                        joinType = JoinType.LEFT,
                        onColumn = Illustrator2PlatformKeysTable.illustrator,
                        otherColumn = IllustratorTable.id
                    ).selectAll()
                        .apply {
                            illustrator.platformKeyMap.forEach { platform, key ->
                                orWhere {
                                    (PlatformKeyTable.platform eq platform) and (PlatformKeyTable.key eq key)
                                }
                            }
                        }

                    // create
                    newIllustratorId = illustratorQuery.firstOrNull()?.let {
                        it[IllustratorTable.id].value
                    } ?: IllustratorTable.insert {
                        it[name] = illustrator.name
                    }.let {
                        it[IllustratorTable.id].value
                    }

                    val platformIds = PlatformKeyTable.batchInsert(
                        illustrator.platformKeyMap.toList(),
                        ignore = true
                    ) { (platform, key) ->
                        this[PlatformKeyTable.platform] = platform
                        this[PlatformKeyTable.key] = key
                    }.mapNotNull {
                        it.getOrNull(PlatformKeyTable.id)?.value
                    }

                    Illustrator2PlatformKeysTable.batchInsert(
                        platformIds,
                        ignore = true
                    ) {
                        this[Illustrator2PlatformKeysTable.illustrator] = newIllustratorId
                        this[Illustrator2PlatformKeysTable.platformkey] = it
                    }
                }

                // TODO:: use insertReturning?
                TagTable.batchInsert(
                    pic.tags,
                    ignore = true
                ) {
                    this[TagTable.name] = it
                }

                newTagIds.addAll(
                    TagTable.select(
                        TagTable.id
                    ).where {
                        TagTable.name inList pic.tags
                    }.map {
                        it[TagTable.id].value
                    }
                )
            }


            val newPicId = PicEntity.new {
                this.filename = pic.filename
                this.hash = hash
            }.id.value

            newIllustratorId?.let { newIllustratorId ->
                Pic2IllustratorTable.insert {
                    it[picId] = newPicId
                    it[illustratorId] = newIllustratorId
                }
            }

            Pic2TagsTable.batchInsert(
                newTagIds,
                ignore = true
            ) {
                this[Pic2TagsTable.picId] = newPicId
                this[Pic2TagsTable.tagId] = it
            }

        }
    }

    suspend fun updatePic(
        pic: Pic,
    ) = transaction(db) {
        PicEntity.findSingleByAndUpdate(PicTable.filename eq pic.filename) { picEntity ->

            var newIllustratorId: Int? = null
            val newTagIds = mutableListOf<Int>()
            transaction(MetadataDB.db) {
                pic.illustrator?.let { illustrator ->
                    val illustratorQuery = PlatformKeyTable.join(
                        otherTable = Illustrator2PlatformKeysTable,
                        joinType = JoinType.LEFT,
                        onColumn = PlatformKeyTable.id,
                        otherColumn = Illustrator2PlatformKeysTable.platformkey
                    ).join(
                        otherTable = IllustratorTable,
                        joinType = JoinType.LEFT,
                        onColumn = Illustrator2PlatformKeysTable.illustrator,
                        otherColumn = IllustratorTable.id
                    ).selectAll()
                        .apply {
                            illustrator.platformKeyMap.forEach { platform, key ->
                                orWhere {
                                    (PlatformKeyTable.platform eq platform) and (PlatformKeyTable.key eq key)
                                }
                            }
                        }

                    // create
                    newIllustratorId = illustratorQuery.firstOrNull()?.let {
                        it[IllustratorTable.id].value
                    } ?: IllustratorTable.insert {
                        it[name] = illustrator.name
                    }.let {
                        it[IllustratorTable.id].value
                    }

                    val platformIds = PlatformKeyTable.batchInsert(
                        illustrator.platformKeyMap.toList(),
                        ignore = true
                    ) { (platform, key) ->
                        this[PlatformKeyTable.platform] = platform
                        this[PlatformKeyTable.key] = key
                    }.mapNotNull {
                        it.getOrNull(PlatformKeyTable.id)?.value
                    }

                    Illustrator2PlatformKeysTable.batchInsert(
                        platformIds,
                        ignore = true
                    ) {
                        this[Illustrator2PlatformKeysTable.illustrator] = newIllustratorId
                        this[Illustrator2PlatformKeysTable.platformkey] = it
                    }
                }

                // TODO:: use insertReturning?
                TagTable.batchInsert(
                    pic.tags,
                    ignore = true
                ) {
                    this[TagTable.name] = it
                }

                newTagIds.addAll(
                    TagTable.select(
                        TagTable.id
                    ).where {
                        TagTable.name inList pic.tags
                    }.map {
                        it[TagTable.id].value
                    }
                )
            }

            newIllustratorId?.let { newIllustratorId ->
                Pic2IllustratorTable.upsert {
                    it[picId] = picEntity.id
                    it[illustratorId] = newIllustratorId
                }
            }

            Pic2TagsTable.deleteWhere {
                (Pic2TagsTable.picId eq picEntity.id) and (Pic2TagsTable.tagId notInList newTagIds)
            }

            Pic2TagsTable.batchInsert(
                newTagIds,
                ignore = true
            ) {
                this[Pic2TagsTable.picId] = picEntity.id
                this[Pic2TagsTable.tagId] = it
            }
        }
    }

    suspend fun selectPic(
        filename: String,
    ) = transaction(db) {
        PicTable.selectAll()
            .where {
                PicTable.filename eq filename
            }.map {
                it[PicTable.filename]
            }
    }

    companion object {
        val dbs = mutableListOf<StorageDB>()

        fun byNameNoEx(
            nameNoEx: String,
        ) = dbs.find { it.nameNoEx == nameNoEx }

        fun random() = dbs.randomOrNull()

        // TODO::
        suspend fun randomPic(
            storageLabels: Set<String>,
            count: Int,
            illustratorIds: Set<Int?>,
            tags: Set<String> = setOf(),
        ): List<Pic> {
            val storages = storageLabels.mapNotNull { byNameNoEx(it) }
            val storageLabels = storages.map { it.nameNoEx }

            return transaction(MetadataDB.db) {

                storageLabels.forEach {
                    exec(
                        """
                    ATTACH DATABASE './data/databases/storage/${it}.db'
                    AS $it
                    """.trimIndent()
                    )
                }

                val result = PicTable
                    .join(
                        otherTable = Pic2IllustratorTable,
                        joinType = JoinType.LEFT,
                        onColumn = PicTable.id,
                        otherColumn = Pic2IllustratorTable.picId,
                    ).join(
                        otherTable = IllustratorTable,
                        joinType = JoinType.LEFT,
                        onColumn = Pic2IllustratorTable.illustratorId,
                        otherColumn = IllustratorTable.id,
                    ).join(
                        otherTable = Pic2TagsTable,
                        joinType = JoinType.LEFT,
                        onColumn = PicTable.id,
                        otherColumn = Pic2TagsTable.picId,
                    ).join(
                        otherTable = TagTable,
                        joinType = JoinType.LEFT,
                        onColumn = Pic2TagsTable.tagId,
                        otherColumn = TagTable.id,
                    )
                    .select(
                        PicTable.filename
                    ).where {
                        TagTable.name inList tags
                    }
                    .map {
                        it[PicTable.filename]
                    }

                emptyList()
            }
        }

        suspend fun backup() {
            dbs.forEach { db ->
                (db.db.connector().connection as Connection).use { connection ->
                    connection.createStatement().use { statement ->
                        val sql = "VACUUM INTO './data/databases/${db.db.name}.db.bak'"
                        statement.executeUpdate(sql)
                    }
                }
            }
        }
    }
}