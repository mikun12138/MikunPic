package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.isRegularFile
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.dto.awesome.FileExtension
import me.mikun.mikunpic.dto.awesome.dfs
import me.mikun.mikunpic.dto.data.Illustrator
import me.mikun.mikunpic.dto.data.Platform

private class PlaceHolder(
    val type: Type,
) {
    enum class Type {
        Simple,
        IllustratorName,
        IllustratorPixiv,
        IllustratorTwitter,
        Filename
    }
}

private fun makeUploadRule(s: String): List<PlaceHolder> {
    val split = s.split("/")

    return split.map {
        PlaceHolder(
            type =
                if (it.startsWith("{") and it.endsWith("}")) {
                    when (it.removePrefix("{").removeSuffix("}")) {
                        "illustrator_name" -> PlaceHolder.Type.IllustratorName
                        "pixiv" -> PlaceHolder.Type.IllustratorPixiv
                        "twitter" -> PlaceHolder.Type.IllustratorTwitter
                        "filename" -> PlaceHolder.Type.Filename
                        else -> PlaceHolder.Type.Simple
                    }
                } else {
                    PlaceHolder.Type.Simple
                }
        )

    }

}

private val uploadRule = makeUploadRule("{illustrator_name}/{pixiv}/{filename}")

@Composable
fun BoxScope.ManageOverview() {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.align(Alignment.Center),
    ) {
        ElevatedCard {
            ElevatedButton(
                onClick = {
                    scope.launch {
                        Client.sync()
                    }
                },
            ) {
                Text("Sync")
            }
        }

        ElevatedCard {
            ElevatedButton(
                onClick = {
                    scope.launch {
                        fun isImage(file: PlatformFile): Boolean {
                            require(file.isRegularFile())
                            return FileExtension.image.any { ext ->
                                file.name.endsWith(ext, ignoreCase = true)
                            }
                        }

                        FileKit.openDirectoryPicker()?.let { dir ->
                            dfs(
                                dir,
                                PlatformFile::isRegularFile,
                                PlatformFile::list,
                            ) { file, path ->

                                if (path.size != uploadRule.size) {
                                    return@dfs
                                }

                                var illustratorName: String? = null
                                var illustratorPixiv: String? = null
                                var illustratorTwitter: String? = null

                                val dirnames = path.map { it.name }
                                for (i in 0 until path.size) {
                                    when (uploadRule[i].type) {
                                        PlaceHolder.Type.Simple -> {}
                                        PlaceHolder.Type.IllustratorName -> {
                                            illustratorName = dirnames[i]
                                        }

                                        PlaceHolder.Type.IllustratorPixiv -> {
                                            illustratorPixiv = dirnames[i]
                                        }

                                        PlaceHolder.Type.IllustratorTwitter -> {
                                            illustratorTwitter = dirnames[i]
                                        }

                                        PlaceHolder.Type.Filename -> {}
                                    }
                                }

                                if (isImage(file)) {
                                    val illustrator = Illustrator(
                                        id = null,
                                        name = illustratorName,
                                        platformKeyMap = buildMap {
                                            illustratorPixiv?.let { put(Platform.Pixiv, it) }
                                            illustratorTwitter?.let {
                                                put(
                                                    Platform.Twitter,
                                                    it
                                                )
                                            }
                                        }
                                    )
                                    Client.uploadPic(
                                        file.name,
                                        file.readBytes(),
                                        illustrator = illustrator
                                    )
                                }
                            }
                        }
                    }
                },
            ) {
                Text("Upload")
            }
        }
    }
}
