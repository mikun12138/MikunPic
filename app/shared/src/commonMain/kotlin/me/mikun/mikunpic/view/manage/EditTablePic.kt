package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.dto.data.Pic


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditTablePic() {
    val scope = rememberCoroutineScope()

    var picOnTable by remember { mutableStateOf<Pic?>(null) }
    val editContext = object {
        var illustrator by remember(picOnTable) { mutableStateOf(picOnTable?.illustrator) }
        var tags =
            remember(picOnTable) { picOnTable?.tags?.toMutableStateList() ?: mutableStateListOf() }
        var isEdited = remember(
            picOnTable,
            illustrator,
            tags
        ) {
            derivedStateOf {
                picOnTable != null
                        && (
                        picOnTable?.illustrator != illustrator
                                || picOnTable?.tags?.toSet() != tags.toSet()
                        )
            }
        }
    }

    var showBottomSheetIllustrator by remember { mutableStateOf(false) }
    var showBottomSheetTag by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var selectedIndex by remember { mutableStateOf<Int?>(null) }

                val buttons = List<@Composable () -> Unit>(2) { index ->
                    {
                        when (index) {
                            0 -> {
                                ToggleButton(
                                    checked = selectedIndex == index,
                                    onCheckedChange = {
                                        selectedIndex = index
                                        scope.launch {
                                            picOnTable = Client.randomPic(
                                                1,
                                                illustrators = listOf("")

                                            ).pics.firstOrNull()
                                        }
                                    },
                                ) {
                                    Text("No author")
                                }
                            }

                            1 -> {
                                ToggleButton(
                                    checked = selectedIndex == index,
                                    onCheckedChange = {
                                        selectedIndex = index
                                        scope.launch {
                                            picOnTable = Client.randomPic(
                                                1,
                                                tags = listOf("")
                                            ).pics.firstOrNull()
                                        }
                                    },
                                ) {
                                    Text("No tag")
                                }
                            }

                            2 -> {
                                ToggleButton(
                                    checked = selectedIndex == index,
                                    onCheckedChange = {
                                        selectedIndex = index
                                        scope.launch {
                                            picOnTable = Client.randomPic(
                                                1,
                                            ).pics.firstOrNull()
                                        }
                                    },
                                ) {
                                    Text("Random")
                                }
                            }

                            else -> error("")
                        }
                    }
                }

                buttons.forEach {
                    it()
                }
            }

            if (picOnTable != null) {
                Box(
                    modifier = Modifier
                        .weight(0.6f),
                ) {
                    AsyncImage(
                        "${Client.baseUrl}/pic/${picOnTable?.filename}",
                        contentDescription = null,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("illustrator: ")

                        ElevatedAssistChip(
                            onClick = { },
                            label = {
                                editContext.illustrator?.let {
                                    Text(it)
                                }
                            },
                        )

                        ElevatedButton(
                            onClick = {
                                scope.launch {
                                    showBottomSheetIllustrator = true
                                }
                            },
                        ) {
                            Text("Edit Illustrator")
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("tags: ")

                        editContext.tags.forEach { tag ->
                            ElevatedAssistChip(
                                onClick = { },
                                label = {
                                    Text(tag)
                                },
                            )
                        }

                        ElevatedButton(
                            onClick = {
                                scope.launch {
                                    showBottomSheetTag = true
                                }
                            },
                        ) {
                            Text("Edit Tags")
                        }
                    }

                    ElevatedButton(
                        onClick = {
                            if (editContext.isEdited.value) {
                                picOnTable = Pic(
                                    picOnTable!!.filename,
                                    editContext.illustrator,
                                    editContext.tags.toList(),
                                )

                                scope.launch {
                                    Client.updatePic(
                                        picOnTable!!,
                                    )
                                }
                            }
                        },
                    ) {
                        Text("Apply")
                    }
                }
            }
        }

        SearchBottomSheet(
            showBottomSheetIllustrator,
            onCloseSheet = {
                showBottomSheetIllustrator = false
            },
        ) {
            EditPicIllustratorSheet(
                onEditIllustrator = {
                    editContext.illustrator = it
                },
            )
        }

        SearchBottomSheet(
            showBottomSheetTag,
            onCloseSheet = {
                showBottomSheetTag = false
            },
        ) {
            EditPicTagsSheet(
                onEditTag = {
                    editContext.tags.apply {
                        remove(it) || add(it)
                    }
                },
                picTags = picOnTable?.tags,
                editContextTags = editContext.tags
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchBottomSheet(
    showBottomSheet: Boolean,
    onCloseSheet: () -> Unit,
    innerEditSheet: @Composable ColumnScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState()
    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp),
            onDismissRequest = {
                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        onCloseSheet()
                    }
                }
            },
            sheetState = bottomSheetState,
        ) {
            innerEditSheet()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.EditPicIllustratorSheet(
    onEditIllustrator: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberContainedSearchBarState()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    val searchResults = remember { mutableStateListOf<String>() }

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = {
                    scope.launch { searchBarState.animateToCollapsed() }
                    scope.launch {
                        searchResults.clear()
                        searchResults.addAll(
                            Client.searchIllustrator(
                                count = 100,
                                keyword = textFieldState.text.toString(),
                            ).illustrators.map { it.name },
                        )
                    }
                },
            )
        }

    AppBarWithSearch(
        scrollBehavior = scrollBehavior,
        state = searchBarState,
        inputField = inputField,
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        searchResults.forEach {
            ElevatedAssistChip(
                onClick = { onEditIllustrator(it) },
                label = { Text(it) },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.EditPicTagsSheet(
    onEditTag: (String) -> Unit,
    picTags: List<String>?,
    editContextTags: SnapshotStateList<String>,
) {
    val scope = rememberCoroutineScope()

    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberContainedSearchBarState()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    val searchResults = remember { mutableStateListOf<String>() }

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = {
                    scope.launch { searchBarState.animateToCollapsed() }
                    scope.launch {
                        searchResults.clear()
                        searchResults.addAll(
                            Client.searchTag(
                                count = 100,
                                keyword = textFieldState.text.toString(),
                            ).tags,
                        )
                    }
                },
            )
        }

    AppBarWithSearch(
        scrollBehavior = scrollBehavior,
        state = searchBarState,
        inputField = inputField,
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (picTags.isNullOrEmpty()) {
            ElevatedAssistChip(
                onClick = { },
                label = { },
                modifier = Modifier.alpha(0.0f)
            )
        } else {

            /*
                unchange
             */
            picTags.intersect(editContextTags.toSet()).forEach {
                ElevatedAssistChip(
                    onClick = { onEditTag(it) },
                    label = { Text(it) },
                )
            }

            /*
                toAdd
             */
            (editContextTags - picTags.toSet()).forEach {
                ElevatedAssistChip(
                    onClick = { onEditTag(it) },
                    label = { Text(it) },
                    colors = AssistChipDefaults.elevatedAssistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                )
            }

            /*
                toRemove
             */
            (picTags - editContextTags.toSet()).forEach {
                ElevatedAssistChip(
                    onClick = { onEditTag(it) },
                    label = { Text(it) },
                    colors = AssistChipDefaults.elevatedAssistChipColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                )
            }


        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        (searchResults - editContextTags).forEach {
            ElevatedAssistChip(
                onClick = { onEditTag(it) },
                label = { Text(it) },
            )
        }
    }
}
