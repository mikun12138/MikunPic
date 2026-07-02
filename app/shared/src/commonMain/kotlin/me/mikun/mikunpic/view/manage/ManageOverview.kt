package me.mikun.mikunpic.view.manage

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.isRegularFile
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import me.mikun.mikunpic.LocalPref
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
        Filename,
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
            },
        )
    }
}

@Composable
fun BoxScope.ManageOverview() {
    val scope = rememberCoroutineScope()

    val opdeque = remember { mutableStateListOf<Int>() }

    LaunchedEffect(opdeque) {
        if (opdeque.joinToString("") == "43210") {
            opdeque.clear()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val buttonCount = 5
            val buttonIcons = listOf(
                Icons.Default.MusicNote,
                Icons.Default.LibraryMusic,
                Icons.Default.GraphicEq,
                Icons.Default.Album,
                Icons.AutoMirrored.Filled.QueueMusic,
            )

            val rotations = remember {
                List(5) { Animatable(0f) }
            }

            repeat(buttonCount) { index ->
                IconButton(
                    onClick = {
                        if (opdeque.size >= buttonCount) {
                            opdeque.removeFirst()
                        }
                        opdeque.add(index)

                        scope.launch {
                            rotations[index].animateTo(
                                targetValue = rotations[index].targetValue + 360f,
                                animationSpec = tween(
                                    durationMillis = 600,
                                    easing = FastOutSlowInEasing,
                                ),
                            )
                        }
                    },
                    modifier = Modifier
                        .size(128.dp)
                        .graphicsLayer {
                            rotationY = rotations[index].value
                        }
                        .blur((opdeque.count { it == index } * 1.5f).dp),
                ) {
                    Icon(
                        buttonIcons[index],
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                    )
                }
            }
        }

        Box(
            modifier = Modifier.weight(0.5f),
        ) {
            when (opdeque.joinToString("")) {
                "22222" -> {
                    opdeque.clear()
                }

                "013" -> {
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

                "134" -> {
                    val uploadRuleText = rememberTextFieldState(LocalPref.uploadRule)

                    Column {
                        TextField(uploadRuleText)

                        ElevatedButton(
                            onClick = {
                                LocalPref = LocalPref.copy(
                                    uploadRule = uploadRuleText.text.toString(),
                                )

                                val uploadRule = makeUploadRule(LocalPref.uploadRule)
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

                                            if (!isImage(file)) {
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

                                            val illustrator = Illustrator(
                                                name = illustratorName,
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
                                            Client.uploadPic(
                                                file.name,
                                                file.readBytes(),
                                                illustrator = illustrator,
                                            )
                                        }
                                    }
                                }
                            },
                        ) {
                            Text("Upload")
                        }
                    }
                }

                else -> {}
            }
        }
    }
}
