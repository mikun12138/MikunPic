package me.mikun.mikunpic.component.act

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.component.dialog.SimpleAlertDialog
import me.mikun.mikunpic.dto.data.Storage

@Composable
fun StorageDetailAlertDialog(
    show: Boolean,
    storageToShowDetail: Storage?,
    onDismissRequest: () -> Unit,
    onClose: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var isSyncing by remember { mutableStateOf(false) }

    var showSyncStorageDialog by remember { mutableStateOf(false) }
    var storageToSync by remember { mutableStateOf<Storage?>(null) }

    storageToShowDetail?.let { storage ->
        SyncStorageAlertDialog(
            show = showSyncStorageDialog && storageToSync != null,
            storageToSync = storageToSync,
            onDismissRequest = {
                showSyncStorageDialog = false
                storageToSync = null
            },
            onSyncClicked = { pathRule ->
                showSyncStorageDialog = false
                storageToSync = null
                isSyncing = true
                scope.launch {
                    isSyncing = true
                    val result = runCatching {
                        Client.sync(
                            storageLabel = storage.label,
                            syncRuleText = pathRule
                        )
                    }

                    if (result.getOrNull()?.status == HttpStatusCode.OK) {
                        onClose()
                    }
                    isSyncing = false
                }
            },
            onClose = {
                showSyncStorageDialog = false
                storageToSync = null
            }
        )

        SimpleAlertDialog(
            show = show,
            onDismissRequest = onDismissRequest
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                        showSyncStorageDialog = true
                        storageToSync = storageToShowDetail
                    }
                ) {
                    if (!isSyncing) {
                        Text("Sync")
                    } else {
                        LoadingIndicator()
                    }
                }
            }
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
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SyncStorageAlertDialog(
    show: Boolean,
    storageToSync: Storage?,
    onDismissRequest: () -> Unit,
    onSyncClicked: (String) -> Unit,
    onClose: () -> Unit,
) {
    storageToSync?.let { storage ->
        SimpleAlertDialog(
            show = show,
            onDismissRequest = onDismissRequest
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val pathRule = rememberTextFieldState(storage.pathRule)

                Text("sync storage: ${storage.label}")

                OutlinedTextField(
                    state = pathRule
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilledTonalButton(
                        onClick = {
                            onSyncClicked(pathRule.text.toString())
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Y")
                    }

                    FilledTonalButton(
                        onClick = {
                            onClose()
                        }
                    ) {
                        Text("N")
                    }
                }
            }
        }
    }
}