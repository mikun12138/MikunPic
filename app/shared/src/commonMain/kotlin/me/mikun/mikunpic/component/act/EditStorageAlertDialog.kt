package me.mikun.mikunpic.component.act

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import me.mikun.mikunpic.view.manage.StorageType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStorageAlertDialog(
    show: Boolean,
    storageToEdit: Storage?,
    onDismissRequest: () -> Unit,
    onClose: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    storageToEdit?.let { storage ->
        SimpleAlertDialog(
            show = show,
            onDismissRequest = onDismissRequest,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var onSubmitting by remember { mutableStateOf(false) }
                var expanded by remember { mutableStateOf(false) }
                var storageType by remember {
                    mutableStateOf(
                        when (storage) {
                            is Storage.Local -> {
                                StorageType.Local
                            }

                            is Storage.Cos -> {
                                StorageType.Cos
                            }
                        },
                    )
                }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = it
                    },
                ) {
                    val textFieldState = rememberTextFieldState(storageType.value)
                    TextField(
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        state = textFieldState,
                        readOnly = true,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        label = { Text("type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        },
                    ) {
                        DropdownMenuItem(
                            text = { Text("local") },
                            onClick = {
                                storageType = StorageType.Local
                                textFieldState.setTextAndPlaceCursorAtEnd(storageType.value)
                                expanded = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("cos") },
                            onClick = {
                                storageType = StorageType.Cos
                                textFieldState.setTextAndPlaceCursorAtEnd(storageType.value)
                                expanded = false
                            },
                        )
                    }
                }

                when (storageType) {
                    StorageType.Local -> {
                        val storageLocal = storage as? Storage.Local
                        val form = object {
                            var path = rememberTextFieldState(storageLocal?.path ?: "")
                        }

                        listOf(
                            form.path to "path",
                        ).forEach { (state, label) ->
                            TextField(
                                state = state,
                                label = { Text(label) },
                                isError = state.text.isBlank(),
                                colors = TextFieldDefaults.colors(
                                    focusedLabelColor = LocalContentColor.current.copy(alpha = 0.6f),
                                    unfocusedLabelColor = LocalContentColor.current.copy(alpha = 0.4f),
                                ),
                            )
                        }

                        FilledTonalButton(
                            enabled = !onSubmitting,
                            onClick = {
                                scope.launch {
                                    onSubmitting = true

                                    val result = runCatching {
                                        Client.editStorage(
                                            Storage.Local(
                                                label = storage.label,
                                                path = form.path.text.toString(),
                                            ),
                                        )
                                    }

                                    if (result.getOrNull()?.status == HttpStatusCode.OK) {
                                        onClose()
                                    }

                                    onSubmitting = false
                                }
                            },
                        ) {
                            Text("OK")
                            if (onSubmitting) {
                                LoadingIndicator()
                            }
                        }
                    }

                    StorageType.Cos -> {
                        val storageCos = storage as? Storage.Cos
                        val form = object {
                            var secretId = rememberTextFieldState()
                            var secretKey = rememberTextFieldState()
                            var bucketName = rememberTextFieldState(storageCos?.bucketName ?: "")
                            var region = rememberTextFieldState(storageCos?.region ?: "")
                        }

                        listOf(
                            form.secretId to "secretId",
                            form.secretKey to "secretKey",
                            form.bucketName to "bucketName",
                            form.region to "region",
                        ).forEachIndexed { index, (state, label) ->
                            TextField(
                                state = state,
                                label = { Text(label) },
                                // TODO::
                                isError = index > 1 && state.text.isBlank(),
                                colors = TextFieldDefaults.colors(
                                    focusedLabelColor = LocalContentColor.current.copy(alpha = 0.6f),
                                    unfocusedLabelColor = LocalContentColor.current.copy(alpha = 0.4f),
                                ),
                            )
                        }

                        FilledTonalButton(
                            enabled = !onSubmitting,
                            onClick = {
                                scope.launch {
                                    onSubmitting = true
                                    val result = runCatching {
                                        Client.editStorage(
                                            Storage.Cos(
                                                label = storage.label,
                                                secretId = form.secretId.text.toString(),
                                                secretKey = form.secretKey.text.toString(),
                                                bucketName = form.bucketName.text.toString(),
                                                region = form.region.text.toString(),
                                            ),
                                        )
                                    }

                                    if (result.getOrNull()?.status == HttpStatusCode.OK) {
                                        onClose()
                                    }
                                }

                                onSubmitting = false
                            },
                        ) {
                            Text("OK")
                            if (onSubmitting) {
                                LoadingIndicator()
                            }
                        }
                    }

                    else -> {
                    }
                }
            }
        }
    }
}
