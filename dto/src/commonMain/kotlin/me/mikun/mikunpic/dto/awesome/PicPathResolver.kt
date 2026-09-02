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
        var illustratorPixiv: String? = null
        var illustratorTwitter: String? = null

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

            if (holders.any { it.type == UploadRule.PlaceHolder.Type.IllustratorPixiv }) {
                illustratorPixiv =
                    matchResult?.groups["pixiv"]?.value
            }

            if (holders.any { it.type == UploadRule.PlaceHolder.Type.IllustratorTwitter }) {
                illustratorTwitter =
                    matchResult?.groups["twitter"]?.value
            }
        }

        val illustrator =
            illustratorName?.let { name ->
                Illustrator(
                    name = name,
                    platformKeyMap = buildMap {
                        illustratorPixiv?.let {
                            put(
                                Platform.Pixiv,
                                it,
                            )
                        }
                        illustratorTwitter?.let {
                            put(
                                Platform.Twitter,
                                it,
                            )
                        }
                    },
                )
            }

        val platform =
            if (illustratorPixiv != null) {
                Platform.Pixiv.value
            } else if (illustratorTwitter != null) {
                Platform.Twitter.value
            } else {
                Platform.Other.value
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
