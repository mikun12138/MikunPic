package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults.overscrollEffect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.dto.data.Illustrator
import me.mikun.mikunpic.dto.data.Pic
import kotlin.collections.emptyList
import kotlin.repeat

private enum class Edit {
    Pic,
    Illustrator,
    Tag,
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun BoxScope.EditTable() {
    var isFloatingActionButtonMenuExpand by remember { mutableStateOf(false) }
    var editType by remember { mutableStateOf(Edit.Pic) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButtonMenu(
                expanded = isFloatingActionButtonMenuExpand,
                button = {
                    ToggleFloatingActionButton(
                        checked = isFloatingActionButtonMenuExpand,
                        onCheckedChange = {
                            isFloatingActionButtonMenuExpand = it
                        },
                    ) {
                    }
                },
            ) {
                FloatingActionButtonMenuItem(
                    onClick = {
                        editType = Edit.Pic
                    },
                    text = {
                        Text("Pic")
                    },
                    icon = {
                        Icon(
                            Icons.Default.ArtTrack,
                            null,
                        )
                    },
                )

                FloatingActionButtonMenuItem(
                    onClick = {
                        editType = Edit.Illustrator
                    },
                    text = {
                        Text("Illustrator")
                    },
                    icon = {
                        Icon(
                            Icons.Default.PersonSearch,
                            null,
                        )
                    },
                )

                FloatingActionButtonMenuItem(
                    onClick = {
                        editType = Edit.Tag
                    },
                    text = {
                        Text("Tag")
                    },
                    icon = {
                        Icon(
                            Icons.Default.Bookmark,
                            null,
                        )
                    },
                )
            }
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
        ) {

            when (editType) {
                Edit.Pic -> {
                    EditTablePic()
                }

                Edit.Illustrator -> {
                    EditTableIllustrator()
                }

                else -> {}
            }
        }
    }
}
