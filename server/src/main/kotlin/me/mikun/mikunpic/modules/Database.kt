package me.mikun.mikunpic.modules

import io.ktor.server.application.Application
import me.mikun.mikunpic.LocalMikunPicConfig
import me.mikun.mikunpic.database.StorageDB
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

fun Application.configureDatabase() {
    File("./data/databases/storage").apply {
        exists() || mkdirs()
    }

    StorageDB.dbs.addAll(
        LocalMikunPicConfig.storages.map {
            StorageDB(
                Database.connect(
                    "jdbc:sqlite:./data/databases/storage/${it.label}.db",
                    driver = "org.sqlite.JDBC"
                )
            )
        }
    )
}
