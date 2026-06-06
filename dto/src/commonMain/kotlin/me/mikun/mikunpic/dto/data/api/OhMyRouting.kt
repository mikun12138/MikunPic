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
        class Filename(val filename: String) : OhMyRouting {
            override val parent = Pic()
        }
    }

    @Resource("/manage")
    class Manage : OhMyRouting {
        override val parent = OhMyRouting.Companion

        @Resource("/pic")
        class Pic : OhMyRouting {
            override val parent = Manage()

            @Resource("/random")
            data class Random(
                val count: Int,
                val illustrator: String? = null,
                val tags: List<String>? = null,
            ) : OhMyRouting {
                override val parent = Pic()

                @Serializable
                data class Response(
                    @SerialName("pics")
                    val pics: List<me.mikun.mikunpic.dto.data.Pic>,
                )
            }

            @Resource("/upload")
            class Upload : OhMyRouting {
                override val parent = Pic()
            }

            @Resource("/update")
            class Update : OhMyRouting {
                override val parent = Pic()

                @Serializable
                data class Body(
                    @SerialName("pic")
                    val pic: me.mikun.mikunpic.dto.data.Pic
                )
            }
        }


        @Resource("/illustrator")
        class Illustrator : OhMyRouting {
            override val parent = Manage()

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
            ) : OhMyRouting {
                override val parent = Illustrator()

                @Serializable
                data class Response(
                    @SerialName("illustrators")
                    val illustrators: List<String>,
                )
            }


            @Resource("/create")
            class Create : OhMyRouting {
                override val parent = Illustrator()

                @Serializable
                data class Body(
                    @SerialName("name")
                    val name: String,
                )
            }
        }
    }
}
