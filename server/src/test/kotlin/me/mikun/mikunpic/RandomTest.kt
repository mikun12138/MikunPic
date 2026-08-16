package me.mikun.mikunpic

import kotlin.test.Test

private data class PlaceHolder(
    val type: Type,
    val value: String = "",
) {
    enum class Type {
        Simple,
        Unknown,
        IllustratorName,
        IllustratorPixiv,
        IllustratorTwitter,
        Filename,
    }
}

private fun makeUploadRule(s: String): List<List<PlaceHolder>> {
    val split = s.split("/")

    return split.map {
        val result = mutableListOf<StringBuilder>()

        var counter = 0
        var builder = StringBuilder()

        for (c in it) {
            when (c) {
                '{' -> {
                    if (counter == 0) {
                        result.add(builder)
                        builder = StringBuilder()
                    }
                    counter++

                    builder.append(c)
                }

                '}' -> {
                    builder.append(c)

                    if (counter > 0) {
                        counter--
                        if (counter == 0) {
                            result.add(builder)
                            builder = StringBuilder()
                        }
                    }
                }

                else -> {
                    builder.append(c)
                }
            }
        }
        result.add(builder)

        result.filter { it.isNotEmpty() }.map {
            val type =
                if (it.startsWith("{") and it.endsWith("}")) {
                    when (it.removePrefix("{").removeSuffix("}")) {
                        "illustratorName" -> PlaceHolder.Type.IllustratorName
                        "pixiv" -> PlaceHolder.Type.IllustratorPixiv
                        "twitter" -> PlaceHolder.Type.IllustratorTwitter
                        "filename" -> PlaceHolder.Type.Filename
                        else -> PlaceHolder.Type.Unknown
                    }
                } else {
                    PlaceHolder.Type.Simple
                }
            PlaceHolder(
                type = type,
                value = it.toString()
            )
        }
    }
}

private fun List<PlaceHolder>.asRegex(): Regex {
    return buildString {
        this@asRegex.forEach {
            when (it.type) {
                PlaceHolder.Type.Simple -> {
                    append(Regex.escape(it.value))
                }

                else -> {
                    val v = it.value.removePrefix("{").removeSuffix("}")
                    append("""(?<$v>.+?)""")
                }
            }
        }
    }.let {
        Regex(it)
    }
}


class RandomTest {
    @Test
    fun test() {
        println()
        val split = "ana/pixiv_123/456.jpg".split("/")
        var illustratorName: String? = null
        var pixiv: String? = null
        var filename: String? = null
        makeUploadRule("{illustratorName}/pixiv_{pixiv}/{filename}").forEachIndexed { index, holders ->
            println(holders)
            holders.asRegex().let {
                val matchResult = it.matchEntire(split[index])
                if (holders.any { it.type == PlaceHolder.Type.IllustratorName }) {
                    illustratorName = matchResult?.groups["illustratorName"]?.value
                }

                if (holders.any { it.type == PlaceHolder.Type.IllustratorPixiv }) {
                    pixiv = matchResult?.groups["pixiv"]?.value
                }

                if (holders.any { it.type == PlaceHolder.Type.Filename }) {
                    filename = matchResult?.groups["filename"]?.value
                }
            }
        }

        println(illustratorName)
        println(pixiv)
        println(filename)

        println()

    }
}