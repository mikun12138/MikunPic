package me.mikun.mikunpic.dto.awesome

import me.mikun.mikunpic.dto.awesome.UploadRule.asRegex
import me.mikun.mikunpic.dto.data.Illustrator
import me.mikun.mikunpic.dto.data.PicCreate
import me.mikun.mikunpic.dto.data.Platform

class PicPathResolver(
    ruleText: String,
) {
    init {
        require(ruleText.isNotBlank())
    }

    val uploadRule =
        UploadRule.makeUploadRule(ruleText)

    fun <T> resolve(
        path: List<T>,
        filename: (T) -> String,
    ): PicCreate? {
        if (path.size != uploadRule.size) {
            return null
        }

        val file = path.last()

        var illustratorName: String? = null
        var platform: String? = null
        var uid: String? = null

        val dirnames = path.map { filename(it) }

        for (i in path.indices) {
            val holders = uploadRule[i]
            val regex = holders.asRegex()
            val matchResult =
                regex.matchEntire(dirnames[i])
            if (holders.any { it.type == UploadRule.PlaceHolder.Type.IllustratorName }) {
                illustratorName =
                    matchResult?.groups["illustratorName"]?.value
            }

            if (holders.any { it.type == UploadRule.PlaceHolder.Type.Platform }) {
                platform = matchResult?.groups["platform"]?.value
            }

            if (holders.any { it.type == UploadRule.PlaceHolder.Type.Uid }) {
                uid = matchResult?.groups["uid"]?.value
            }
        }

        val illustrator =
            illustratorName?.let { name ->
                Illustrator(
                    name = name,
                    platformKeyMap = buildMap {
                        if (platform != null && uid != null) {
                            put(
                                platform,
                                uid
                            )
                        }
                    },
                )
            }

        val storeKey =
            path.joinToString("/") { pathItem ->
                filename(pathItem)
            }

        return PicCreate(
            filename = filename(file),
            storeKey = storeKey,
            platform = platform,
            illustrator = illustrator,
        )
    }
}
