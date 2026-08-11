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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Size
import io.ktor.http.encodeURLPathPart
import kotlinx.coroutines.launch
import me.mikun.mikunpic.LocalConfig
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.dto.data.Illustrator
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.viewmodel.ManageViewModel
import kotlin.collections.emptyList
import kotlin.collections.mutableListOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditTablePic(
    manageViewModel: ManageViewModel = viewModel { ManageViewModel() },
) {
    val scope = rememberCoroutineScope()

    val localPlatformContext = LocalPlatformContext.current

    var picOnTable by remember { mutableStateOf<Pic?>(null) }

    var currentPicStorageLabel by remember { mutableStateOf("") }

    val currentStorageLabel = manageViewModel.currentStorageLabel.collectAsState().value

    LaunchedEffect(Unit) {
        Client.randomPic(
            count = 1,
            storageLabels = listOf(currentStorageLabel)
        )?.let {
            picOnTable = it.pics.firstOrNull()
            currentPicStorageLabel = it.storageLabel
        }
    }

    val editContext = object {
        var tags =
            remember(picOnTable) { picOnTable?.tags?.toMutableStateList() ?: mutableStateListOf() }
        var isEdited = remember(
            picOnTable,
            tags,
        ) {
            derivedStateOf {
                picOnTable != null &&
                        (
                                picOnTable?.tags?.toSet() != tags.toSet()
                                )
            }
        }
    }

    var showBottomSheetTag by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HeaderSelection(
                onSelectionRandom = {
                    scope.launch {
                        Client.randomPic(
                            count = 1,
                            storageLabels = listOf(currentStorageLabel)
                        )?.let {
                            picOnTable = it.pics.firstOrNull()
                            currentPicStorageLabel = it.storageLabel
                        }
                    }
                },
                onSelectionNoAuthor = {
                    scope.launch {
                        Client.randomPic(
                            count = 1,
                            storageLabels = listOf(currentStorageLabel),
                            illustrators = listOf(Illustrator.UnExist),
                        )?.let {
                            picOnTable = it.pics.firstOrNull()
                            currentPicStorageLabel = it.storageLabel
                        }
                    }
                },
                onSelectionNoTag = {
                    scope.launch {
                        Client.randomPic(
                            count = 1,
                            storageLabels = listOf(currentStorageLabel),
                            tags = listOf(""),
                        )?.let {
                            picOnTable = it.pics.firstOrNull()
                            currentPicStorageLabel = it.storageLabel
                        }
                    }
                },
            )

            if (picOnTable != null) {
                Box(
                    modifier = Modifier
                        .weight(0.6f),
                ) {

                    fun encodePathKeepingSlash(path: String): String {
                        return path
                            .split("/")
                            .joinToString("/") { segment ->
                                segment.encodeURLPathPart()
                            }
                    }

                    PicShowingTable(
                        ImageRequest.Builder(localPlatformContext)
                            .data(
                                "${LocalConfig.current.server}/pic/${
                                    encodePathKeepingSlash(
                                        picOnTable!!.filename
                                    )
                                }"
                            )
                            .size(Size.ORIGINAL)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .diskCachePolicy(CachePolicy.DISABLED)
//                            .precision(Precision.EXACT)
                            .build(),
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
                                picOnTable?.illustrator?.let {
                                    Text(it.name)
                                }
                            },
                        )
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

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ElevatedButton(
                            onClick = {
                                if (editContext.isEdited.value) {
                                    picOnTable = Pic(
                                        picOnTable!!.filename,
                                        picOnTable!!.illustrator,
                                        editContext.tags.toList(),
                                    )

                                    scope.launch {
                                        Client.updatePic(
                                            storageLabel = currentPicStorageLabel,
                                            picOnTable!!,
                                        )
                                    }
                                }
                            },
                        ) {
                            Text("Apply")
                        }

                        Text("To Storage: $currentPicStorageLabel")
                    }
                }
            }
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
                editContextTags = editContext.tags,
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
private fun ColumnScope.EditPicTagsSheet(
    onEditTag: (String) -> Unit,
    picTags: List<String>?,
    editContextTags: SnapshotStateList<String>,
) {
    val scope = rememberCoroutineScope()

    val textFieldState = rememberTextFieldState()

    val searchResults by produceState(mutableListOf()) {
        value = Client.searchTag(
            count = 100
        )?.let {
            it.tags.toMutableList()
        } ?: mutableListOf()
    }

    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val searchBarState = rememberContainedSearchBarState()
    val inputField = @Composable {
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
                        )?.tags ?: emptyList(),
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
        if (picTags.isNullOrEmpty() && editContextTags.isEmpty()) {
            ElevatedAssistChip(
                onClick = { },
                label = { },
                modifier = Modifier.alpha(0.0f),
            )
        } else {
            /*
                unchange
             */
            picTags!!.intersect(editContextTags.toSet()).forEach {
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
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
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
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        (searchResults - editContextTags - (picTags?.toSet() ?: emptySet())).forEach {
            ElevatedAssistChip(
                onClick = { onEditTag(it) },
                label = { Text(it) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HeaderSelection(
    onSelectionRandom: () -> Unit,
    onSelectionNoAuthor: () -> Unit,
    onSelectionNoTag: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var selectedIndex by remember { mutableStateOf(0) }

        val buttons = List<@Composable () -> Unit>(3) { index ->
            {
                when (index) {
                    0 -> {
                        ToggleButton(
                            checked = selectedIndex == index,
                            onCheckedChange = {
                                selectedIndex = index
                                onSelectionRandom()
                            },
                        ) {
                            Text("Random")
                        }
                    }

                    1 -> {
                        ToggleButton(
                            checked = selectedIndex == index,
                            onCheckedChange = {
                                selectedIndex = index
                                onSelectionNoAuthor()
                            },
                        ) {
                            Text("No author")
                        }
                    }

                    2 -> {
                        ToggleButton(
                            checked = selectedIndex == index,
                            onCheckedChange = {
                                selectedIndex = index
                                onSelectionNoTag()
                            },
                        ) {
                            Text("No tag")
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
}

@Composable
private fun PicShowingTable(
    model: Any,
) {
    AsyncImage(
        model,
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize(),
        contentScale = ContentScale.Fit,
    )
}
