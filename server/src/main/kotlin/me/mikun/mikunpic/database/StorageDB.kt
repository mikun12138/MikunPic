package me.mikun.mikunpic.database

import me.mikun.mikunpic.database.table.IllustratorTable
import me.mikun.mikunpic.database.table.PicTable
import me.mikun.mikunpic.database.table.TagTable
import me.mikun.mikunpic.database.table.relation.Illustrator2PlatformKeysTable
import me.mikun.mikunpic.database.table.relation.Pic2IllustratorTable
import me.mikun.mikunpic.database.table.relation.Pic2TagsTable
import me.mikun.mikunpic.dto.data.Illustrator
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.name
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.sql.Connection
import kotlin.use

class StorageDB(
    val db: Database,
) {
    init {
        transaction(db) {
            SchemaUtils.create(
                PicTable,
                IllustratorTable,
                TagTable,
                Pic2IllustratorTable,
                Pic2TagsTable,
                Illustrator2PlatformKeysTable,
            )
        }
    }

    val nameNoEx
        get() = db.name.removeSuffix(".db")

    val countPic
        get() = transaction(db) {
            PicEntity.count()
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
        illustrator: String?,
    ) {
        transaction(db) {
            illustrator?.let {
                IllustratorEntity.new {
                    this.name = illustrator
                }
            }
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
                        emptyMap(),
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
                        emptyMap(),
                    )
                }
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

    private suspend fun searchTag(
        keyword: String? = null,
    ): List<String> = transaction(db) {
        if (!keyword.isNullOrEmpty()) {
            TagEntity
                .find { TagTable.name like "%$keyword%" }
                .map { it.name }
        } else {
            TagEntity
                .all()
                .map { it.name }
        }
    }

    companion object {
        val dbs = mutableListOf<StorageDB>()

        fun byNameNoEx(
            nameNoEx: String,
        ) = dbs.find { it.nameNoEx == nameNoEx }

        fun random() = dbs.randomOrNull()

        suspend fun searchIllustrator(
            count: Int,
            keyword: String? = null,
            page: Int = 0,
        ): List<Illustrator> {

        }

        suspend fun searchTag(
            count: Int,
            keyword: String? = null,
            page: Int = 0,
        ): List<String> {
            val tags = dbs.flatMap {
                it.searchTag(
                    keyword
                )
            }
                .toSortedSet()
                .toList()

            val maxPage = (tags.size - 1) / count

            val relPage = page.coerceIn(0, maxPage)

            return tags.subList(
                relPage * count,
                ((relPage + 1) * count).coerceAtMost(tags.size)
            )
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