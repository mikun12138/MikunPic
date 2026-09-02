package me.mikun.mikunpic.component.dialog

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAlertDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (show) {
        BasicAlertDialog(
            onDismissRequest = onDismissRequest,
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                content()
            }
        }
    }
}
