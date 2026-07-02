package me.mikun.mikunpic.storage

import io.ktor.server.application.Application
import io.ktor.server.application.log
import kotlinx.coroutines.flow.Flow
import me.mikun.mikunpic.LocalMikunPicConfig
import me.mikun.mikunpic.dto.awesome.FileExtension
import me.mikun.mikunpic.dto.data.MikunPicConfig
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import java.io.InputStream
import java.util.concurrent.CopyOnWriteArraySet

sealed class PicStorage {
    protected val picKeys =
        object : CopyOnWriteArraySet<String>() {

            private fun isValid(e: String?): Boolean = e != null &&
                    FileExtension.image.any {
                        e.endsWith(
                            it,
                            ignoreCase = true,
                        )
                    }

            override fun add(e: String?): Boolean = isValid(e) && super.add(e)

            override fun addAll(elements: Collection<String>): Boolean {
                val valid = elements.filter { isValid(it) }
                return super.addAll(valid)
            }
        }

    companion object {
        lateinit var delegate: PicStorage

        val picKeys
            get() = delegate.picKeys

        fun configure(application: Application) {
            if (LocalMikunPicConfig.storage == null) {
                return
            }

            picKeys.clear()

            runCatching {
                when (LocalMikunPicConfig.storage) {
                    is MikunPicConfig.Storage.Local -> {
                        delegate =
                            PicStorageLocal().apply {
                                init(application)
                            }
                    }

                    is MikunPicConfig.Storage.Cos -> {
                        delegate =
                            PicStorageCos().apply {
                                init(application)
                            }
                    }

                    else -> error("??? how can you reach here ???")
                }
            }.onFailure { e ->
                application.log.error(e.message)
                throw e
            }

            application.log.info("PicStorage count: ${delegate.picKeys.size}")
        }

        suspend fun random(): InputStream? = delegate.random()

        suspend fun upload(
            byteArray: ByteArray,
            filename: String,
        ) = delegate.upload(
            byteArray,
            filename,
        )

        suspend fun byName(
            name: String,
            thumbnail: OhMyRouting.Pic.Filename.Thumbnail = OhMyRouting.Pic.Filename.Thumbnail.Orig,
        ): InputStream? = delegate.byName(
            name,
            thumbnail,
        )
    }

    abstract fun init(application: Application)

    abstract suspend fun random(): InputStream?

    abstract suspend fun byName(
        name: String,
        thumbnail: OhMyRouting.Pic.Filename.Thumbnail = OhMyRouting.Pic.Filename.Thumbnail.Orig,
    ): InputStream?

    abstract suspend fun upload(
        byteArray: ByteArray,
        filename: String,
    )
}
