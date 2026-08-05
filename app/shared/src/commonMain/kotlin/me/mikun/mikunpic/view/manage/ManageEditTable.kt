package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import me.mikun.mikunpic.viewmodel.ManageViewModel

private enum class Edit {
    Pic,
    Illustrator,
    Tag,
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BoxScope.ManageEditTable(
    manageViewModel: ManageViewModel = viewModel { ManageViewModel() }
) {
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
                if (editType != Edit.Pic) {
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
                }
                if (editType != Edit.Illustrator) {

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
                }
                if (editType != Edit.Tag) {
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

                Edit.Tag -> {
                    EditTableTag()
                }

                else -> {}
            }
        }
    }
}
