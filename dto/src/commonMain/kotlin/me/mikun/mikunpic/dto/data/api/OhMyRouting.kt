package me.mikun.mikunpic.dto.data.api

import io.ktor.resources.Resource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

        @Resource("{filename}")
        class Filename(
            val filename: String,
            val thumbnail: Thumbnail = Thumbnail.Orig,
        ) : OhMyRouting {
            override val parent = Pic()

            enum class Thumbnail {
                Thumb,
                Small,
                Medium,
                Large,
                Orig
            }
        }
    }

    @Resource("/manage")
    class Manage : OhMyRouting {
        override val parent = OhMyRouting.Companion

        @Resource("/pic")
        class Pic : OhMyRouting {
            override val parent = Manage()

            @Resource("/upload")
            class Upload : OhMyRouting {
                override val parent = Pic()
            }

            @Resource("/random")
            data class Random(
                val count: Int,
                val illustrators: QueryParameterList<String> = emptyList(),
                val tags: QueryParameterList<String> = emptyList(),
            ) : OhMyRouting {
                override val parent = Pic()

                @Serializable
                data class Response(
                    @SerialName("pics")
                    val pics: List<me.mikun.mikunpic.dto.data.Pic>,
                )
            }

            @Resource("/update")
            class Update : OhMyRouting {
                override val parent = Pic()

                @Serializable
                data class Body(
                    @SerialName("pic")
                    val pic: me.mikun.mikunpic.dto.data.Pic,
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
                    @SerialName("name")
                    val name: String,
                )
            }

            @Resource("/random")
            data class Random(
                val count: Int,
            ) : OhMyRouting {
                override val parent = Illustrator()

                @Serializable
                data class Response(
                    @SerialName("illustrators")
                    val illustrators: List<String>,
                )
            }

            @Resource("/search")
            class Search(
                val count: Int,
                val keyword: String,
                val page: Int,
            ) : OhMyRouting {
                override val parent = Illustrator()

                @Serializable
                data class Response(
                    @SerialName("illustrators")
                    val illustrators: List<me.mikun.mikunpic.dto.data.Illustrator>,
                )
            }

            @Resource("{illustrator_id}")
            class IllustratorId(
                @SerialName("illustrator_id")
                val illustratorId: Int,
            ) : OhMyRouting {
                override val parent = Illustrator()

                @Serializable
                data class Response(
                    val illustrator: me.mikun.mikunpic.dto.data.Illustrator,
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

            @Resource("/search")
            class Search(
                val count: Int,
                val keyword: String,
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
