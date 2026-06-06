package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.dto.data.Pic

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditTable() {
    val scope = rememberCoroutineScope()

    var picOnTable by remember { mutableStateOf<Pic?>(null) }
    val editContext = object {
        var illustrator by remember(picOnTable) { mutableStateOf(picOnTable?.illustrator) }
        var isEdited by remember(
            picOnTable,
            illustrator
        ) { mutableStateOf(picOnTable != null && picOnTable?.illustrator != illustrator) }
    }

    var showBottomSheet by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var selectedIndex by remember { mutableStateOf(0) }

                val buttons = List<@Composable () -> Unit>(2) { index ->
                    {
                        when (index) {
                            0 -> {
                                ToggleButton(
                                    checked = selectedIndex == index,
                                    onCheckedChange = {
                                        selectedIndex = index
                                        scope.launch {
                                            picOnTable = Client.randomPic(
                                                1,
                                                null
                                            ).pics.firstOrNull()
                                        }
                                    },
                                ) {
                                    Text("No author")
                                }
                            }

                            1 -> {
                                ToggleButton(
                                    checked = selectedIndex == index,
                                    onCheckedChange = {
                                        selectedIndex = index
                                        scope.launch {
                                            // TODO::
//                                            picOnTable = Client.randomPic(
//                                                1,
//                                                null
//                                            ).pics.firstOrNull()
                                        }
                                    },
                                ) {
                                    Text("No tag")
                                }
                            }

                            else -> error("")
                        }
                    }
                }

                buttons.forEach {
                    it()
                }

            }

            if (picOnTable != null) {
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                ) {
                    AsyncImage(
                        "${Client.baseUrl}/pic/${picOnTable?.filename}",
                        contentDescription = null
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("illustrator: ")

                        ElevatedAssistChip(
                            onClick = { },
                            label = {
                                editContext.illustrator?.let {
                                    Text(it)
                                }
                            }
                        )

                        ElevatedButton(
                            onClick = {
                                scope.launch {
                                    showBottomSheet = true
                                }
                            }
                        ) {
                            Text("Edit")
                        }
                    }

                    ElevatedButton(
                        onClick = {
                            if (editContext.isEdited) {
                                picOnTable = Pic(
                                    picOnTable!!.filename,
                                    editContext.illustrator,
                                    emptyList()
                                )

                                scope.launch {
                                    Client.updatePic(
                                        picOnTable!!
                                    )
                                }
                            }
                        }
                    ) {
                        Text("Apply")
                    }
                }
            }
        }

        SearchBottomSheet(
            showBottomSheet,
            onCloseSheet = {
                showBottomSheet = false
            },
            onEditIllustrator = {
                editContext.illustrator = it
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchBottomSheet(
    showBottomSheet: Boolean,
    onCloseSheet: () -> Unit,
    onEditIllustrator: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState()
    if (showBottomSheet) {

        ModalBottomSheet(
            modifier = Modifier
                .fillMaxHeight(),
            onDismissRequest = {
                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        onCloseSheet()
                    }
                }
            },
            sheetState = bottomSheetState,
        ) {

            val textFieldState = rememberTextFieldState()
            val searchBarState = rememberContainedSearchBarState()
            val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

            val searchResults = remember { mutableStateListOf<String>() }

            val inputField =
                @Composable {
                    SearchBarDefaults.InputField(
                        textFieldState = textFieldState,
                        searchBarState = searchBarState,
                        onSearch = {
                            scope.launch { searchBarState.animateToCollapsed() }
                            scope.launch {
                                searchResults.clear()
                                searchResults.addAll(
                                    Client.searchIllustrator(
                                        count = 100,
                                        keyword = textFieldState.text.toString()
                                    ).illustrators
                                )
                            }
                        },
                    )
                }

            AppBarWithSearch(
                scrollBehavior = scrollBehavior,
                state = searchBarState,
                inputField = inputField,
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                searchResults.forEach {
                    ElevatedAssistChip(
                        onClick = { onEditIllustrator(it) },
                        label = { Text(it) }
                    )
                }
            }
        }
    }
}
