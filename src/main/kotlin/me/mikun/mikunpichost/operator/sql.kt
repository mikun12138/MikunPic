package me.mikun.mikunpichost.operator

import me.mikun.mikunpichost.database.IllustratorEntity
import me.mikun.mikunpichost.database.IllustratorTable
import me.mikun.mikunpichost.database.PicEntity
import me.mikun.mikunpichost.database.PicTable
import me.mikun.mikunpichost.database.TagEntity
import me.mikun.mikunpichost.database.TagTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

suspend fun createPic(
    filename: String,
    hash: String,
    illustratorEntity: IllustratorEntity?,
    tags: List<TagEntity>
): PicEntity {
    return transaction {
        PicEntity.new {
            this.filename = filename
            this.hash = hash
            this.illustrator = illustratorEntity
            this.tags = SizedCollection(tags)
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
    pic: PicEntity,
    illustrator: String?,
    tags: List<String>,
) {
    val illustrator = illustrator?.let { selectOrCreateIllustrator(it) }
    val tags = SizedCollection(tags.map { selectOrCreateTag(it) })

    return transaction {
        pic.tags = tags
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
