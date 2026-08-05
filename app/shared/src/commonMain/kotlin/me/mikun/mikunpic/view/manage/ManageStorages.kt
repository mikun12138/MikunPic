package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.shadow
import io.ktor.util.collections.getValue
import me.mikun.mikunpic.dto.data.Storage
import me.mikun.mikunpic.viewmodel.ManageStorageViewModel
import me.mikun.mikunpic.viewmodel.ManageViewModel

@Composable
fun ManageStorages(
    viewModel: ManageStorageViewModel = viewModel { ManageStorageViewModel() },
    manageViewModel: ManageViewModel = viewModel { ManageViewModel() },
) {
    val storages by viewModel.storages.collectAsState()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {

            }
        ) {
            Text("Add")
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            val currentStorageLabel by manageViewModel.currentStorageLabel.collectAsState()

            storages.forEach { storage ->
                StorageCard(storage, onToggleStorage = { label ->
                        manageViewModel.switchStorage(label)
                    }, currentStorageLabel)
            }
        }
    }
}

@Composable
private fun StorageCard(
    storage: Storage,
    onToggleStorage: (String) -> Unit,
    currentStorageLabel: String,
) {
    val selected = storage.label == currentStorageLabel
    ElevatedCard(
        onClick = {
            onToggleStorage(storage.label)
        },
        modifier = Modifier.padding(8.dp)
            .then(
                if (selected) {
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
