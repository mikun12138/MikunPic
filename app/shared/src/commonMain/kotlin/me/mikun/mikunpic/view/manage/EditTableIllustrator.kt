package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.dto.data.Illustrator
import me.mikun.mikunpic.dto.data.Pic
import me.mikun.mikunpic.dto.data.api.OhMyRouting

private class PageContext(
    val illustratorContexts: List<IllustratorContext?>,
)

private class IllustratorContext(
    val illustrator: Illustrator,
    val pics: List<Pic>,
    val picCaches: List<ImageRequest>,
)

@Composable
fun EditTableIllustrator() {
    val illustratorCount = 20
    val picPreIllustrator = 5
    var pageIndex by remember { mutableStateOf(0) }

    val localPlatformContext = LocalPlatformContext.current
    val pageContext by produceState(
        initialValue = PageContext(List(illustratorCount) { null }),
        pageIndex,
    ) {
        value = PageContext(List(illustratorCount) { null })

        val illustrators = Client.searchIllustrator(
            count = illustratorCount,
            page = pageIndex,
        ).illustrators

        val contexts = MutableList<IllustratorContext?>(illustrators.size) {
            IllustratorContext(
                illustrator = illustrators[it],
                pics = emptyList(),
                picCaches = emptyList(),
            )
        }

        value = PageContext(contexts.toList())

        supervisorScope {
            illustrators.forEachIndexed { index, illustrator ->
                launch {
                    val pics = Client.randomPic(
                        picPreIllustrator,
                        illustrators = listOf(illustrator)
                    ).pics

                    val picCaches = pics.map { pic ->
                        val bytes = Client.fetchPic(
                            pic.filename,
                            OhMyRouting.Pic.Filename.Thumbnail.Thumb
                        )

                        ImageRequest.Builder(localPlatformContext)
                            .data(bytes)
                            .memoryCacheKey(pic.filename)
                            .crossfade(true)
                            .build()
                    }

                    val context = IllustratorContext(
                        illustrator = illustrator,
                        pics = pics,
                        picCaches = picCaches,
                    )

                    contexts[index] = context

                    value = PageContext(contexts.toList())
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = pageContext.illustratorContexts,
            ) {
                IllustratorCard(
                    it,
                )
            }
        }
    }
}

@Composable
private fun IllustratorCard(
    illustratorContext: IllustratorContext?,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = Alignment.CenterVertically
            ),
        ) {
            Box(
                modifier = Modifier.weight(0.4f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = illustratorContext?.illustrator?.name ?: "Loading...",
                    style = typography.headlineLarge,
                )
            }

            Box(
                modifier = Modifier.weight(0.6f),
                contentAlignment = Alignment.Center
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(
                        illustratorContext?.picCaches.orEmpty(),
                    ) { cacheReq ->
                        AsyncImage(
                            cacheReq,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}
