package me.mikun.mikunpic.view.manage

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import me.mikun.mikunpic.component.act.AddStorageAlertDialog
import me.mikun.mikunpic.component.act.DeleteStorageAlertDialog
import me.mikun.mikunpic.component.act.EditStorageAlertDialog
import me.mikun.mikunpic.component.act.StorageDetailAlertDialog
import me.mikun.mikunpic.component.card.AcrylicCard
import me.mikun.mikunpic.dto.data.Storage
import me.mikun.mikunpic.viewmodel.ManageStorageViewModel
import me.mikun.mikunpic.viewmodel.ManageViewModel

enum class StorageType(
    val value: String,
) {
    None(""),
    Local("local"),
    Cos("cos")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageStorages(
    viewModel: ManageStorageViewModel = viewModel { ManageStorageViewModel() },
    manageViewModel: ManageViewModel = viewModel { ManageViewModel() },
) {
    val storages by viewModel.storages.collectAsState()

    var showAddStorageDialog by remember { mutableStateOf(false) }
    AddStorageAlertDialog(
        show = showAddStorageDialog,
        onDismissRequest = {
            showAddStorageDialog = false
        },
        onClose = {
            showAddStorageDialog = false
            viewModel.flashStorages()
        }
    )

    var showEditStorageDialog by remember { mutableStateOf(false) }
    var storageToEdit by remember { mutableStateOf<Storage?>(null) }
    EditStorageAlertDialog(
        show = showEditStorageDialog && storageToEdit != null,
        storageToEdit = storageToEdit,
        onDismissRequest = {
            showEditStorageDialog = false
            storageToEdit = null
        },
        onClose = {
            showEditStorageDialog = false
            storageToEdit = null
            viewModel.flashStorages()
        }
    )

    var showDeleteStorageDialog by remember { mutableStateOf(false) }
    var storageToDelete by remember { mutableStateOf<Storage?>(null) }

    DeleteStorageAlertDialog(
        show = showDeleteStorageDialog && storageToDelete != null,
        storageToDelete = storageToDelete,
        onDismissRequest = {
            showDeleteStorageDialog = false
            storageToDelete = null
        },
        onClose = {
            showDeleteStorageDialog = false
            storageToDelete = null
            viewModel.flashStorages()
        }
    )

    var showStorageDetailDialog by remember { mutableStateOf(false) }
    var storageToShowDetail by remember { mutableStateOf<Storage?>(null) }

    StorageDetailAlertDialog(
        show = showStorageDetailDialog && storageToShowDetail != null,
        storageToShowDetail = storageToShowDetail,
        onDismissRequest = {
            showStorageDetailDialog = false
            storageToShowDetail = null
        },
        onClose = {
            showStorageDetailDialog = false
            storageToShowDetail = null
        }
    )


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.weight(0.05f),
        ) {
            Button(
                onClick = {
                    showAddStorageDialog = true
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(2.5f)
            ) {
                Text("Add")
            }
        }

        Box(
            modifier = Modifier
                .weight(0.95f)
        ) {
            val currentStorageLabel by manageViewModel.currentStorageLabel.collectAsState()
            LazyVerticalGrid(
                columns = GridCells.Adaptive(256.dp),
                modifier = Modifier
                    .padding(8.dp)
            ) {
                items(
                    items = storages,
                    key = { storage -> storage.label },
                ) { storage ->
                    StorageCard(
                        storage, onToggleStorage = { label ->
                            manageViewModel.switchStorage(label)
                        },
                        storage.label == currentStorageLabel,
                        onEditClicked = {
                            showEditStorageDialog = true
                            storageToEdit = storage
                        },
                        onDeleteClicked = {
                            showDeleteStorageDialog = true
                            storageToDelete = storage
                        },
                        onDetailClicked = {
                            showStorageDetailDialog = true
                            storageToShowDetail = storage
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun StorageCard(
    storage: Storage,
    onToggleStorage: (String) -> Unit,
    isSelected: Boolean,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onDetailClicked: () -> Unit,
) {
    var flipped by remember(storage.label) { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing,
        ),
    )
    val showBack = rotation > 90f
    val density = LocalDensity.current.density

    AcrylicCard(
        onClick = {
            if (!flipped) {
                onToggleStorage(storage.label)
            } else {
                onDetailClicked()
            }
        },
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(16 / 9f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 16f * density
            }
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    if (showBack) {
                        rotationY = 180f
                    }
                },
        ) {
            if (showBack) {
                StorageCardBack(
                    storage = storage,
                    onFlipClicked = {
                        flipped = false
                    },
                )
            } else {
                StorageCardFront(
                    storage = storage,
                    onEditClicked = onEditClicked,
                    onDeleteClicked = onDeleteClicked,
                    onFlipClicked = {
                        flipped = true
                    },
                )
            }
        }
    }
}

@Composable
private fun StorageCardFront(
    storage: Storage,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onFlipClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        IconButton(
            onClick = {
                onFlipClicked()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(40.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Show storage details",
                modifier = Modifier.size(20.dp),
            )
        }

        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Text(storage.label)
            }

            HorizontalDivider()

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterHorizontally,
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                ElevatedButton(
                    onClick = {
                        onEditClicked()
                    },
                    contentPadding = PaddingValues(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                ) {
                    Text("Edit")
                }

                ElevatedButton(
                    onClick = {
                        onDeleteClicked()
                    },
                    contentPadding = PaddingValues(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun StorageCardBack(
    storage: Storage,
    onFlipClicked: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        Text(
            text = "${storage.label} Details",
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        HorizontalDivider()

        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (storage) {
                is Storage.Local -> {
                    StorageInfoRow(
                        label = "Type",
                        value = "Local",
                    )
                    StorageInfoRow(
                        label = "Path",
                        value = storage.path,
                    )
                }

                is Storage.Cos -> {
                    StorageInfoRow(
                        label = "Type",
                        value = "Cos",
                    )
                    StorageInfoRow(
                        label = "Bucket",
                        value = storage.bucketName,
                    )
                    StorageInfoRow(
                        label = "Region",
                        value = storage.region,
                    )
                }
            }
        }

        ElevatedButton(
            onClick = {
                onFlipClicked()
            },
        ) {
            Text("Back")
        }

    }
}

@Composable
private fun StorageInfoRow(
    label: String,
    value: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(0.2f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}