package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import me.mikun.mikunpic.viewmodel.EditTableTagViewModel

private enum class EditMode {
    None,
    Remove,
    Add,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTableTag(
    viewModel: EditTableTagViewModel = viewModel { EditTableTagViewModel() },
) {
    val scope = rememberCoroutineScope()

    val tags by viewModel.tags.collectAsState()

    var editMode by remember { mutableStateOf(EditMode.None) }

    val tagToRemove = remember { mutableStateListOf<String>() }

    LaunchedEffect(editMode) {
        viewModel.flashTagsSelected()
        tagToRemove.clear()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row {
            when (editMode) {
                EditMode.None -> {
                    Button(
                        onClick = {
                            editMode = EditMode.Remove
                        }
                    ) {
                        Text("-")
                    }

                    Button(
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
                                        "sandbox",
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
                                    "sandbox",
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
                                viewModel.updateImageShowing()
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

            fun encodePathKeepingSlash(path: String): String {
                return path
                    .split("/")
                    .joinToString("/") { segment ->
                        segment.encodeURLPathPart()
                    }
            }

            items(imagesShowing) {
                AsyncImage(
                    model = ImageRequest.Builder(localPlatformContext)
                        .data(
                            "${LocalConfig.current.server}/pic/${
                                encodePathKeepingSlash(
                                    it
                                )
                            }"
                        )
                        .size(Size.ORIGINAL)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .diskCachePolicy(CachePolicy.DISABLED)
//                            .precision(Precision.EXACT)
                        .build(),
                    contentDescription = null
                )
            }
        }
    }
}

