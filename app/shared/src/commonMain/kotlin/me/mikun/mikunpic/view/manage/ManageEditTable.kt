package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.mikun.mikunpic.viewmodel.ManageViewModel

private enum class Edit {
    Pic,
    Illustrator,
    Tag,
}

@Composable
fun BoxScope.ManageEditTable(
    manageViewModel: ManageViewModel = viewModel { ManageViewModel() }
) {
    var editType by remember { mutableStateOf(Edit.Pic) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        EditTypeSelector(
            selectedType = editType,
            onSelectedTypeChange = {
                editType = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when (editType) {
                Edit.Pic -> {
                    EditTablePic()
                }

                Edit.Illustrator -> {
                    EditTableIllustrator()
                }

                Edit.Tag -> {
                    EditTableTag()
                }
            }
        }
    }
}

@Composable
private fun EditTypeSelector(
    selectedType: Edit,
    onSelectedTypeChange: (Edit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = remember {
        listOf(
            EditTypeOption(
                type = Edit.Pic,
                label = "Pic",
                icon = Icons.Default.ArtTrack,
            ),
            EditTypeOption(
                type = Edit.Illustrator,
                label = "Illustrator",
                icon = Icons.Default.PersonSearch,
            ),
            EditTypeOption(
                type = Edit.Tag,
                label = "Tag",
                icon = Icons.Default.Bookmark,
            ),
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth(),
        ) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = selectedType == option.type,
                    onClick = {
                        onSelectedTypeChange(option.type)
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size,
                    ),
                    icon = {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(option.label)
                    },
                )
            }
        }
    }
}

private data class EditTypeOption(
    val type: Edit,
    val label: String,
    val icon: ImageVector,
)
