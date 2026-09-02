package me.mikun.mikunpic.view.manage

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import me.mikun.mikunpic.component.card.AcrylicCard
import me.mikun.mikunpic.dto.awesome.FileExtension
import me.mikun.mikunpic.dto.awesome.PicPathResolver
import me.mikun.mikunpic.dto.awesome.UploadRule
import me.mikun.mikunpic.dto.awesome.UploadRule.asRegex
import me.mikun.mikunpic.dto.awesome.dfs
import me.mikun.mikunpic.dto.data.Illustrator
import me.mikun.mikunpic.dto.data.PicCreate
import me.mikun.mikunpic.dto.data.Platform
import me.mikun.mikunpic.viewmodel.ManageStorageViewModel

@Composable
fun BoxScope.ManageOverview(
    manageStorageViewModel: ManageStorageViewModel = viewModel { ManageStorageViewModel() },
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val opdeque = remember { mutableStateListOf<Int>() }

    LaunchedEffect(opdeque) {
        if (opdeque.joinToString("") == "43210") {
            opdeque.clear()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        if (event.changes.any { it.changedToDown() }) {
                            focusManager.clearFocus()
                        }
                    }
                }
            },
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
                    // TODO:: 没活了233
                }

                "134" -> {
                    val uploadRuleText = rememberTextFieldState(LocalPref.uploadRule)

                    var showSelectStorageToUpload by remember { mutableStateOf(false) }

                    AcrylicCard {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp,
                                alignment = Alignment.CenterVertically,
                            ),
                        ) {
                            OutlinedTextField(uploadRuleText)

                            OutlinedButton(
                                onClick = {
                                    showSelectStorageToUpload = true
                                },
                            ) {
                                Text("Upload")
                            }
                        }
                    }

                    if (showSelectStorageToUpload) {
                        val storages by manageStorageViewModel.storages.collectAsState()
                        ElevatedCard(
                            modifier = Modifier.padding(8.dp),
                        ) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.Center,
                            ) {
                                storages.forEach {
                                    Button(
                                        onClick = {
                                            LocalPref = LocalPref.copy(
                                                uploadRule = uploadRuleText.text.toString(),
                                            )

                                            val picPathResolver =
                                                PicPathResolver(LocalPref.uploadRule)
                                            scope.launch {
                                                fun isImage(file: PlatformFile): Boolean {
                                                    require(file.isRegularFile())
                                                    return FileExtension.image.any { ext ->
                                                        file.name.endsWith(
                                                            ext,
                                                            ignoreCase = true,
                                                        )
                                                    }
                                                }

                                                FileKit.openDirectoryPicker()?.let { dir ->
                                                    dfs(
                                                        dir,
                                                        PlatformFile::isRegularFile,
                                                        PlatformFile::list,
                                                    ) { file, path ->
                                                        if (!isImage(file)) {
                                                            return@dfs
                                                        }

                                                        val picCreate = picPathResolver.resolve(
                                                            path = path,
                                                            filename = PlatformFile::name,
                                                        )

                                                        picCreate?.let { picCreate ->
                                                            Client.uploadPic(
                                                                storageLabel = it.label,
                                                                picBytes = file.readBytes(),
                                                                pic = picCreate,
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            showSelectStorageToUpload = false
                                        },
                                    ) {
                                        Text(it.label)
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}
