package me.mikun.mikunpic.component.act

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.component.dialog.SimpleAlertDialog
import me.mikun.mikunpic.dto.data.Storage

@Composable
fun DeleteStorageAlertDialog(
    show: Boolean,
    storageToDelete: Storage?,
    onDismissRequest: () -> Unit,
    onClose: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    storageToDelete?.let { storage ->
        SimpleAlertDialog(
            show = show,
            onDismissRequest = onDismissRequest,
        ) {
            var isDeleting by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("delete storage: ${storage.label}")

                if (isDeleting) {
                    LoadingIndicator()
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilledTonalButton(
                        enabled = !isDeleting,
                        onClick = {
                            scope.launch {
                                isDeleting = true
                                val result = runCatching {
                                    Client.deleteStorage(
                                        storage.label,
                                    )
                                }

                                if (result.getOrNull()?.status == HttpStatusCode.OK) {
                                    onClose()
                                }
                                isDeleting = false
                            }
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                    ) {
                        Text("Y")
                    }

                    FilledTonalButton(
                        enabled = !isDeleting,
                        onClick = {
                            onClose()
                        },
                    ) {
                        Text("N")
                    }
                }
            }
        }
    }
}
