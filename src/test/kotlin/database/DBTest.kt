package me.mikun.database

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.testApplication
import io.ktor.util.Digest
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.runBlocking
import me.mikun.mikunpichost.database.IllustratorEntity
import me.mikun.mikunpichost.database.IllustratorTable
import me.mikun.mikunpichost.database.Pic2TagTable
import me.mikun.mikunpichost.database.PicEntity
import me.mikun.mikunpichost.database.PicTable
import me.mikun.mikunpichost.database.TagEntity
import me.mikun.mikunpichost.database.TagTable
import me.mikun.mikunpichost.storage.PicStorage
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.sqlite.SQLiteConfig
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DBTest {

    companion object {
        lateinit var testPic: PicEntity
        lateinit var testIllustrator: IllustratorEntity
        lateinit var testTag: TagEntity
        lateinit var testTag2: TagEntity
        lateinit var testTag3: TagEntity
    }

    @BeforeTest
    fun beforeTest() {
        File("./test/data/databases").apply {
            exists() || mkdirs()
        }
        File("./test/data/databases/pic.db").apply {
            exists() && delete()
        }
        Database.connect(
            "jdbc:sqlite:./test/data/databases/pic.db",
            driver = "org.sqlite.JDBC",
            setupConnection = { connection ->
                SQLiteConfig().apply {
                    enforceForeignKeys(true)
                }.apply(
                    connection
                )
            }
        )

        transaction {
            SchemaUtils.create(
                PicTable,
            )
        }
    }

    @Test
    fun test() {
        transaction {
            SchemaUtils.create(
                PicTable,
                IllustratorTable,
                TagTable,
                Pic2TagTable
            )

            /*
                use dao api
             */
            testIllustrator = IllustratorEntity.new { name = "test_illustrator" }
            val hashString = runBlocking {
                Digest("md5").let {
                    it += this::class.java.classLoader.getResourceAsStream("test/rua.jpg").readBytes()
                    it.build()
                }.toHexString()
            }
            testPic = PicEntity.new {
                filename = "test/rua.jpg"
                illustrator = testIllustrator
                hash = hashString
            }

            testTag = TagEntity.new { name = "test_tag" }
            testTag2 = TagEntity.new { name = "test_tag2" }
            testTag3 = TagEntity.new { name = "test_tag3" }


            testPic.tags = SizedCollection(
                listOf(
                    testTag,
                    testTag2,
                    testTag3,
                )
            )

            PicEntity.wrapRows(
                (PicTable innerJoin Pic2TagTable innerJoin TagTable)
                    .select(PicTable.id)
                    .where { TagTable.name eq "test_tag" }
                    .withDistinct()
            ).forEach {
                println(it)
                it.tags = SizedCollection(it.tags - TagEntity.find { TagTable.name eq "test_tag2" })
            }

            TagEntity.findSingleByAndUpdate(TagTable.name eq "test_tag3") {
                it.delete()
            }
        }
    }

    @Test
    fun uploadPicTest() = testApplication {
        environment {
            config = ApplicationConfig("application.yaml")
        }
        application {
            PicStorage.configure(this)
        }


        val response = client.post("/upload") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "description",
                            "rua.jpg"
                        )

                        appendInput(
                            key = "pic",
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"rua.jpg\""
                                )
                            }
                        ) {
                            this::class.java.classLoader.getResourceAsStream("rua.jpg").asInput()
                        }
                    }
                )
            )
        }

        assertEquals(
            HttpStatusCode.Created,
            response.status
        )
    }


}