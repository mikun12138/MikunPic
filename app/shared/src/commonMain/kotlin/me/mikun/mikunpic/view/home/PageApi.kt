package me.mikun.mikunpic.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import mikunpic.app.shared.generated.resources.Res
import mikunpic.app.shared.generated.resources.rua
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun PageApi() {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .aspectRatio(maxWidth / maxHeight)
        ) {
            ApiCards(
                *Array(1) {
                    {
                        Card(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth()
                                .aspectRatio(0.66f)
                        ) {

                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {

                                Image(
                                    painter = painterResource(Res.drawable.rua),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier
                                        .padding(16.dp)
                                ) {

                                    Text(
                                        "-随机图-",
                                        style = MaterialTheme.typography.headlineLarge
                                    )

                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = RoundedCornerShape(12.dp),
                                    ) {
                                        SelectionContainer {
                                            Text(
                                                text = "https://pic.mikun.icu/random",
                                                modifier = Modifier.padding(16.dp),
                                                fontFamily = FontFamily.Monospace,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun ApiCards(
    vararg contents: @Composable LazyGridItemScope.() -> Unit,
) {

    BoxWithConstraints {
        val desiredHeight =
            (maxHeight * 0.5f)
                .coerceIn(320.dp, 480.dp)

        LazyHorizontalGrid(
            rows =
                if (contents.size < 4)
                    GridCells.Fixed(1)
                else
                    GridCells.Adaptive(desiredHeight),
            horizontalArrangement =
                Arrangement.spacedBy(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp),
        ) {
            items(
                contents
            ) { content ->
                content(this)
            }
        }
    }
}