package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import me.mikun.mikunpic.viewmodel.EditTableTagViewModel
import me.mikun.mikunpic.viewmodel.ManageViewModel

private enum class EditMode {
    None,
    Remove,
    Add,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTableTag(
    viewModel: EditTableTagViewModel = viewModel { EditTableTagViewModel() },
    manageViewModel: ManageViewModel = viewModel { ManageViewModel() },
) {
    val scope = rememberCoroutineScope()

    val tags by viewModel.tags.collectAsState()

    var editMode by remember { mutableStateOf(EditMode.None) }

    val tagToRemove = remember { mutableStateListOf<String>() }

    val currentStorageLabel by manageViewModel.currentStorageLabel.collectAsState()
    LaunchedEffect(currentStorageLabel) {
        viewModel.updateTags()
        viewModel.updateImageShowing(
            currentStorageLabel
        )
    }

    LaunchedEffect(editMode) {
        viewModel.flashTagsSelected()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row {
            when (editMode) {
                EditMode.None -> {
                    Button(
                        enabled = currentStorageLabel.isNotEmpty(),
                        onClick = {
                            tagToRemove.clear()
                            editMode = EditMode.Remove
                        }
                    ) {
                        Text("-")
                    }

                    Button(
                        enabled = currentStorageLabel.isNotEmpty(),
                        onClick = {
                            editMode = EditMode.Add
                        }
                    ) {
                        Text("+")
                    }
                }

                EditMode.Remove -> {
                    Button(
                        onClick = {
                            scope.launch {
                                tagToRemove.forEach {
                                    Client.deleteTag(
                                        it
                                    )
                                }
                                viewModel.updateTags()
                            }
                            editMode = EditMode.None
                        }
                    ) {
                        Text("OK")
                    }
                }

                EditMode.Add -> {
                    val tagToAdd = rememberTextFieldState()
                    TextField(
                        tagToAdd
                    )

                    Button(
                        onClick = {
                            scope.launch {
                                Client.createTag(
                                    tagToAdd.text.toString()
                                )
                                viewModel.updateTags()
                            }
                            editMode = EditMode.None
                        }
                    ) {
                        Text("OK")
                    }
                }

                else -> {

                }

            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            maxLines = 3
        ) {
            when (editMode) {
                EditMode.Remove -> {
                    tags.forEach { tag ->
                        ElevatedFilterChip(
                            tagToRemove.contains(tag),
                            onClick = {
                                tagToRemove.remove(tag) || tagToRemove.add(tag)
                            },
                            label = {
                                Text(tag)
                            }
                        )
                    }
                }

                else -> {
                    val tagsSelected = viewModel.tagsSelected.collectAsState().value

                    tags.forEach { tag ->
                        ElevatedFilterChip(
                            tagsSelected.contains(tag),
                            onClick = {
                                viewModel.toggleTagsSelected(tag)
                                viewModel.updateImageShowing(
                                    currentStorageLabel
                                )
                            },
                            label = {
                                Text(tag)
                            }
                        )
                    }
                }
            }
        }

        val localPlatformContext = LocalPlatformContext.current

        val imagesShowing = viewModel.imageShowing.collectAsState().value

        LazyHorizontalStaggeredGrid(
            rows = StaggeredGridCells.Fixed(2),
            modifier = Modifier.weight(1f)
        ) {

            items(imagesShowing) { (storageLabel, picId) ->
                // TODO:: no bytes
                val imageBytes by produceState<ByteArray?>(
                    initialValue = null,
                    storageLabel,
                    picId,
                ) {
                    value = Client.fetchPic(
                        id = picId,
                        thumbnail = OhMyRouting.Pic.Thumbnail.Thumb,
                        storageLabel = storageLabel,
                    )
                }

                AsyncImage(
                    model = ImageRequest.Builder(localPlatformContext)
                        .data(imageBytes)
                        .memoryCacheKey("${storageLabel}:${picId}")
                        .build(),
                    contentDescription = null
                )
            }
        }
    }
}
