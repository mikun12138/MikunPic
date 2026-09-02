package me.mikun.mikunpic.modules

import io.ktor.server.application.Application
import me.mikun.mikunpic.LocalMikunPicConfig
import me.mikun.mikunpic.database.MetadataDB
import me.mikun.mikunpic.database.StorageDB
import org.jetbrains.exposed.v1.jdbc.Database
import java.io.File

fun Application.configureDatabase() {
    File("./data/databases").apply {
        exists() || mkdirs()
    }

    MetadataDB.init(
        Database.connect(
            "jdbc:sqlite:./data/databases/metadata.db",
            driver = "org.sqlite.JDBC",
        ),
    )

    File("./data/databases/storage").apply {
        exists() || mkdirs()
    }

    StorageDB.dbs.addAll(
        LocalMikunPicConfig.storages.map {
            StorageDB(
                Database.connect(
                    "jdbc:sqlite:./data/databases/storage/${it.label}.db",
                    driver = "org.sqlite.JDBC",
                ),
            )
        },
    )

//    val storageLabels = StorageDB.dbs.map { it.nameNoEx }
//    storageLabels.forEach { label ->
//        transaction(MetadataDB.db) {
//            exec(
//                """
//                ATTACH DATABASE './data/databases/storage/${label}.db'
//                AS $label
//                """.trimIndent()
//            )
//        }
//    }
}
