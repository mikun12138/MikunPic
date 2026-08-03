package me.mikun.mikunpic.modules

import io.ktor.server.application.Application
import me.mikun.mikunpic.LocalMikunPicConfig
import me.mikun.mikunpic.database.table.IllustratorTable
import me.mikun.mikunpic.database.table.PicTable
import me.mikun.mikunpic.database.table.TagTable
import me.mikun.mikunpic.database.table.relation.Illustrator2PlatformKeysTable
import me.mikun.mikunpic.database.table.relation.Pic2IllustratorTable
import me.mikun.mikunpic.database.table.relation.Pic2TagsTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File

val dbs = mutableListOf<Database>()

fun Application.configureDatabase() {
    File("./data/databases/storage").apply {
        exists() || mkdirs()
    }

    dbs.addAll(
        LocalMikunPicConfig.storages.map {
            Database.connect(
                "jdbc:sqlite:./data/databases/storage/${it.label}.db",
                driver = "org.sqlite.JDBC"
            )
        }
    )

    dbs.forEach { db ->
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
}
