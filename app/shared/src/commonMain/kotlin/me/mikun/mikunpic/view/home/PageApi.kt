package me.mikun.mikunpic.view.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import me.mikun.mikunpic.Config
import me.mikun.mikunpic.LocalConfig
import me.mikun.mikunpic.component.act.PicCardPopup
import me.mikun.mikunpic.component.card.AcrylicCard
import me.mikun.mikunpic.component.image.SizeCachedImage
import me.mikun.mikunpic.view.LocalNavController
import me.mikun.mikunpic.view.Nav
import kotlin.random.Random

@Composable
fun PageApi() {
    var showPicCardPopup by remember { mutableStateOf(false) }
    var picUrlToPopup by remember { mutableStateOf<String?>(null) }
    var picToPopupMemoryCacheKey by remember { mutableStateOf<String?>(null) }
    if (showPicCardPopup && picUrlToPopup != null) {
        PicCardPopup(
            show = showPicCardPopup,
            onDismissRequest = {
                showPicCardPopup = false
                picUrlToPopup = null
            },
            picUrl = picUrlToPopup!!,
            memoryCacheKey = picToPopupMemoryCacheKey,
            diskCacheKey = picToPopupMemoryCacheKey,
        )
    }

    val navController = LocalNavController.current

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .aspectRatio(maxWidth / maxHeight),
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .padding(8.dp),
                ) {
                    val apis = LocalConfig.current.apis
                    BoxWithConstraints {
                        val desiredHeight =
                            (maxHeight * 0.5f)
                                .coerceIn(320.dp, 480.dp)
                        LazyHorizontalGrid(
                            rows =
                                if (apis.size < 8) {
                                    GridCells.Fixed(1)
                                } else {
                                    GridCells.Adaptive(desiredHeight)
                                },
                            horizontalArrangement =
                                Arrangement.spacedBy(16.dp),

                            verticalArrangement =
                                Arrangement.spacedBy(16.dp),
                        ) {
                            items(
                                apis,
                            ) { api ->
                                var randomRefreshKey by remember { mutableStateOf(0) }
                                val cacheKey = "$api$randomRefreshKey"
                                ApiCard(
                                    api = api,
                                    cacheKey = cacheKey,
                                    onClick = {
                                        showPicCardPopup = true
                                        picUrlToPopup = api.url
                                        picToPopupMemoryCacheKey =
                                            cacheKey
                                    },
                                    onTestButtonClick = {
                                        randomRefreshKey = Random.nextInt(Int.MAX_VALUE)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                ElevatedButton(
                    onClick = {
                        navController.navigate(Nav.Manage)
                    },
                ) {
                    Text("Manage")
                }
            }
        }
    }
}

@Composable
private fun LazyGridItemScope.ApiCard(
    api: Config.Api,
    cacheKey: String,
    onClick: () -> Unit,
    onTestButtonClick: () -> Unit,
) {
    AcrylicCard(
        modifier = Modifier
            .animateItem()
            .fillMaxWidth()
            .aspectRatio(0.66f),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {



            AcrylicCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                SizeCachedImage(
                    data = api.url,
                    cacheKey = cacheKey,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onClick()

                        },
                    contentScale = ContentScale.Crop,
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Text(
                    "-随机图-",
                    style = typography.headlineLarge,
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                        alpha = 0.5f
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    SelectionContainer {
                        Text(
                            text = api.name,
                            modifier = Modifier.padding(16.dp),
                            fontFamily = FontFamily.Monospace,
                            style = typography.bodyLarge,
                        )
                    }
                }

                Button(
                    onClick = onTestButtonClick
                ) {
                    Text("Test")
                }

            }
        }
    }
}
