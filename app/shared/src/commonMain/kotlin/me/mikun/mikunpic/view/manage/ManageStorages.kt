package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import me.mikun.mikunpic.component.act.AddStorageAlertDialog
import me.mikun.mikunpic.component.act.DeleteStorageAlertDialog
import me.mikun.mikunpic.component.act.EditStorageAlertDialog
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

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(0.1f),
        ) {
            Button(
                onClick = {
                    showAddStorageDialog = true
                }
            ) {
                Text("Add")
            }
        }

        FlowRow(
            maxItemsInEachRow = 4,
            modifier = Modifier
                .padding(8.dp)
                .weight(0.9f)
        ) {
            val currentStorageLabel by manageViewModel.currentStorageLabel.collectAsState()

            storages.forEach { storage ->
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
                    }
                )
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
) {
    ElevatedCard(
        onClick = {
            onToggleStorage(storage.label)
        },
        modifier = Modifier.padding(8.dp)
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
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Text(storage.label)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
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
