package me.mikun.mikunpic.dto.data.api

import io.ktor.resources.Resource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.mikun.mikunpic.dto.data.MikunPicConfig
import me.mikun.mikunpic.dto.data.Storage

interface OhMyRouting {
    val parent: Any

    @Resource("/")
    companion object

    @Resource("/random")
    class Random : OhMyRouting {
        override val parent = OhMyRouting.Companion
    }

    @Resource("/pic")
    class Pic : OhMyRouting {
        override val parent = OhMyRouting.Companion

        enum class Thumbnail {
            Thumb,
            Small,
            Medium,
            Large,
            Orig,
        }

        @Resource("/id/{id}")
        class Id(
            val id: String,
            val thumbnail: Thumbnail = Thumbnail.Orig,
            val storageLabel: String,
        ) : OhMyRouting {
            override val parent = Pic()
        }

        @Resource("/{platform}/{key}")
        class PlatformKey(
            val platform: String,
            val key: String,
            val thumbnail: Thumbnail = Thumbnail.Orig,
        ): OhMyRouting {
            override val parent = Pic()
        }
    }

    @Resource("/manage")
    class Manage : OhMyRouting {
        override val parent = OhMyRouting.Companion

        @Resource("/storages")
        class Storages : OhMyRouting {
            override val parent = Manage()

            @Serializable
            data class Response(
                @SerialName("storages")
                val storages: List<Storage>,
            )
        }

        @Resource("/config")
        class Config : OhMyRouting {
            override val parent = Manage()

            @Serializable
            data class Body(
                @SerialName("config")
                val mikunPicConfig: MikunPicConfig,
            )
        }

        @Resource("/pic")
        class Pic : OhMyRouting {
            override val parent = Manage()

            @Resource("/upload")
            class Upload : OhMyRouting {
                override val parent = Pic()
            }

            @Resource("/random")
            class Random : OhMyRouting {
                override val parent = Pic()

                @Serializable
                data class Body(
                    @SerialName("count")
                    val count: Int,
                    @SerialName("illustrator_ids")
                    val illustratorIds: QueryParameterList<Int> = emptyList(),
                    @SerialName("tags")
                    val tags: QueryParameterList<String> = emptyList(),
                    @SerialName("storage_label")
                    val storageLabels: QueryParameterList<String> = emptyList(),
                )

                @Serializable
                data class Response(
                    @SerialName("pics_by_storage")
                    val label2Pics: Map<String, Set<me.mikun.mikunpic.dto.data.Pic>> = emptyMap(),
                )
            }

            @Resource("/update")
            class Update : OhMyRouting {
                override val parent = Pic()

                @Serializable
                data class Body(
                    @SerialName("storage_label")
                    val storageLabel: String,
                    @SerialName("pic")
                    val pic: me.mikun.mikunpic.dto.data.PicUpdate,
                )
            }
        }

        @Resource("/illustrator")
        class Illustrator : OhMyRouting {
            override val parent = Manage()

            @Resource("/create")
            class Create : OhMyRouting {
                override val parent = Illustrator()

                @Serializable
                data class Body(
                    @SerialName("illustrator")
                    val illustrator: me.mikun.mikunpic.dto.data.Illustrator,
                )
            }

            @Resource("/search")
            class Search(
                val count: Int,
                val keyword: String,
                val page: Int = 0,
            ) : OhMyRouting {
                override val parent = Illustrator()

                @Serializable
                data class Response(
                    @SerialName("illustrators")
                    val illustrators: List<me.mikun.mikunpic.dto.data.Illustrator>,
                )
            }
        }

        @Resource("/tag")
        class Tag : OhMyRouting {
            override val parent = Manage()

            @Resource("/create")
            class Create : OhMyRouting {
                override val parent = Tag()

                @Serializable
                data class Body(
                    @SerialName("name")
                    val name: String,
                )
            }

            @Resource("/delete")
            class Delete : OhMyRouting {
                override val parent = Tag()

                @Serializable
                data class Body(
                    @SerialName("name")
                    val name: String,
                )
            }

            @Resource("/search")
            class Search(
                val count: Int,
                val keyword: String,
                val page: Int = 0,
            ) : OhMyRouting {
                override val parent = Tag()

                @Serializable
                data class Response(
                    @SerialName("tags")
                    val tags: List<String>,
                )
            }
        }

        @Resource("/backup")
        class Backup : OhMyRouting {
            override val parent = Manage()
        }

        @Resource("/async")
        class Sync : OhMyRouting {
            override val parent = Manage()
        }
    }
}
