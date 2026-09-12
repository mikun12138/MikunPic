package me.mikun.mikunpic

import me.mikun.mikunpic.dto.awesome.UploadRule
import me.mikun.mikunpic.dto.awesome.UploadRule.asRegex
import kotlin.test.Test


class RandomTest {
    @Test
    fun test() {
        println()
        val split = "ana/pixiv_123/456.jpg".split("/")
        var illustratorName: String? = null
        var platform: String? = null
        var uid: String? = null
        var filename: String? = null
        UploadRule.makeUploadRule("{illustratorName}/{platform}_{uid}/{filename}").forEachIndexed { index, holders ->
            println(holders)
            holders.asRegex().let {
                val matchResult = it.matchEntire(split[index])
                if (holders.any { it.type == UploadRule.PlaceHolder.Type.IllustratorName }) {
                    illustratorName = matchResult?.groups["illustratorName"]?.value
                }

                if (holders.any { it.type == UploadRule.PlaceHolder.Type.Platform }) {
                    platform = matchResult?.groups["platform"]?.value
                }

                if (holders.any { it.type == UploadRule.PlaceHolder.Type.Platform }) {
                    uid = matchResult?.groups["uid"]?.value
                }

                if (holders.any { it.type == UploadRule.PlaceHolder.Type.Filename }) {
                    filename = matchResult?.groups["filename"]?.value
                }
            }
        }

        println(illustratorName)
        println(platform)
        println(uid)
        println(filename)

        println()
    }
}
