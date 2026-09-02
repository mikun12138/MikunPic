package me.mikun.mikunpic.database

import me.mikun.mikunpic.database.table.IllustratorTable
import me.mikun.mikunpic.database.table.PlatformKeyTable
import me.mikun.mikunpic.database.table.TagTable
import me.mikun.mikunpic.database.table.relation.Illustrator2PlatformKeysTable
import me.mikun.mikunpic.dto.data.Illustrator
import me.mikun.mikunpic.dto.data.Platform
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.collections.set

object MetadataDB {
    lateinit var db: Database

    fun init(
        db: Database,
    ) {
        this.db = db
        transaction(db) {
            SchemaUtils.create(
                IllustratorTable,
                PlatformKeyTable,
                Illustrator2PlatformKeysTable,

                TagTable,
            )
        }
    }

    val countIllustrator
        get() = transaction(db) {
            IllustratorEntity.count()
        }

    val countTag
        get() = transaction(db) {
            TagEntity.count()
        }

    suspend fun createIllustrator(
        illustrator: Illustrator,
    ) {
        suspendTransaction(db) {
            illustrator.let {
                IllustratorEntity.new {
                    this.name = illustrator.name
                    this.platformKeys = SizedCollection(
                        illustrator.platformKeyMap.map { (platform, key) ->
                            PlatformKeyEntity.new {
                                this.platform = platform
                                this.key = key
                            }
                        },
                    )
                }
            }
        }
    }

    // TODO:: null or notnull? get or post?
    suspend fun searchIllustrator(
        count: Int,
        keyword: String? = null,
        page: Int = 0,
    ): List<Illustrator> = suspendTransaction(db) {
        data class MutableIllustrator(
            val id: Int,
            val name: String,
            val platformKeyMap: MutableMap<Platform, String>,
        )

        val result = mutableMapOf<Int, MutableIllustrator>()
        val offset = (page * count).coerceAtLeast(0).toLong()
        IllustratorTable.join(
            otherTable = Illustrator2PlatformKeysTable,
            joinType = JoinType.LEFT,
            onColumn = IllustratorTable.id,
            otherColumn = Illustrator2PlatformKeysTable.illustrator,
        ).join(
            otherTable = PlatformKeyTable,
            joinType = JoinType.LEFT,
            onColumn = Illustrator2PlatformKeysTable.platformkey,
            otherColumn = PlatformKeyTable.id,
        ).select(
            IllustratorTable.id,
            IllustratorTable.name,
            PlatformKeyTable.platform,
            PlatformKeyTable.key,
        ).apply {
            if (keyword != null) {
                where { IllustratorTable.name like "%$keyword%" }
            }
        }
            .orderBy(IllustratorTable.id to SortOrder.ASC)
            .limit(count)
            .offset(offset)
            .forEach { row ->
                val id = row[IllustratorTable.id].value

                val illustrator = result.getOrPut(id) {
                    MutableIllustrator(
                        id,
                        row[IllustratorTable.name],
                        mutableMapOf(),
                    )
                }

                illustrator.platformKeyMap[row[PlatformKeyTable.platform]] =
                    row[PlatformKeyTable.key]
            }
        result.values.map {
            Illustrator(
                id = it.id,
                name = it.name,
                platformKeyMap = it.platformKeyMap.toMap(),
            )
        }
    }

    suspend fun createTag(
        tag: String?,
    ) {
        suspendTransaction(db) {
            tag?.let {
                TagEntity.new {
                    this.name = tag
                }
            }
        }
    }

    suspend fun deleteTag(
        tag: String,
    ) {
        suspendTransaction(db) {
            TagEntity.find { TagTable.name eq tag }
                .firstOrNull()?.let {
                    it.delete()
                }
        }
    }

    suspend fun searchTag(
        count: Int,
        keyword: String? = null,
        page: Int = 0,
    ): List<String> = suspendTransaction(db) {
        val offset = (page * count).coerceAtLeast(0).toLong()
        if (!keyword.isNullOrEmpty()) {
            TagEntity
                .find { TagTable.name like "%$keyword%" }
                .limit(count)
                .offset(offset)
                .map { it.name }
        } else {
            TagEntity
                .all()
                .limit(count)
                .offset(offset)
                .map { it.name }
        }
    }
}
