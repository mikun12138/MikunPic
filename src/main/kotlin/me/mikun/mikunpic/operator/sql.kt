package me.mikun.mikunpic.operator

import me.mikun.mikunpic.database.IllustratorEntity
import me.mikun.mikunpic.database.IllustratorTable
import me.mikun.mikunpic.database.PicEntity
import me.mikun.mikunpic.database.PicTable
import me.mikun.mikunpic.database.TagEntity
import me.mikun.mikunpic.database.TagTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

internal object Sql {
    suspend fun createPic(
        filename: String,
        hash: String,
        illustratorEntity: IllustratorEntity?,
        tags: SizedCollection<TagEntity>
    ): PicEntity {
        return transaction {
            PicEntity.new {
                this.filename = filename
                this.hash = hash
                this.illustrator = illustratorEntity
                this.tags = tags
            }
        }
    }

    suspend fun selectPic(
        filename: String
    ): PicEntity? {
        return transaction {
            PicEntity.find { PicTable.filename eq filename }.firstOrNull()
        }
    }

    suspend fun updatePic(
        picEntity: PicEntity,
        illustratorEntity: IllustratorEntity?,
        tagEntities: SizedCollection<TagEntity>,
    ) {
        return transaction {
            picEntity.illustrator = illustratorEntity
            picEntity.tags = tagEntities
        }
    }

    suspend fun createIllustrator(
        name: String
    ): IllustratorEntity {
        return transaction {
            IllustratorEntity.new {
                this.name = name
            }
        }
    }

    suspend fun selectIllustrator(
        name: String
    ): IllustratorEntity? {
        return transaction {
            IllustratorEntity.find { IllustratorTable.name eq name }.firstOrNull()
        }
    }

    suspend fun selectOrCreateIllustrator(
        name: String
    ): IllustratorEntity {
        return selectIllustrator(name) ?: createIllustrator(name)
    }

    suspend fun createTag(
        name: String
    ): TagEntity {
        return transaction {
            TagEntity.new {
                this.name = name
            }
        }
    }

    suspend fun selectTag(
        name: String
    ): TagEntity? {
        return transaction {
            TagEntity.find { TagTable.name eq name }.firstOrNull()
        }
    }

    suspend fun selectOrCreateTag(
        name: String
    ): TagEntity {
        return selectTag(name) ?: createTag(name)
    }
}
