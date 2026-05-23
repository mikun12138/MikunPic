package me.mikun.mikunpic.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter.State.Empty.painter
import coil3.compose.rememberAsyncImagePainter
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PicCarousel(
    painters: List<Painter>,
) {

    val actualSize = painters.size

    val carouselState = rememberCarouselState(
        initialItem =
            Int.MAX_VALUE / 2 -
                    (Int.MAX_VALUE / 2) % actualSize
    ) {
        Int.MAX_VALUE
    }

    var fadeIn by remember {
        mutableStateOf(true)
    }

    val blurRadius by animateDpAsState(
        targetValue = if (
            carouselState.isScrollInProgress || fadeIn
        ) 0.dp else 20.dp,

        animationSpec = tween(600),
    )

    val foregroundAlpha by animateFloatAsState(
        targetValue = if (
            carouselState.isScrollInProgress || fadeIn
        ) 0f else 1f,

        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
    )

    LaunchedEffect(Unit) {
        fadeIn = false
    }

    val clipShape = MaterialTheme.shapes.extraLarge

    val currentItem = carouselState.currentItem

    fun shouldLoad(index: Int): Boolean {
        return abs(index - currentItem) <= 2
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        HorizontalCenteredHeroCarousel(
            state = carouselState,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            itemSpacing = 8.dp,
        ) { index ->

            val realIndex =
                index % actualSize

            if (shouldLoad(index)) {
                val painter = painters[realIndex]

                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .maskClip(clipShape)
                        .blur(blurRadius),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                )

                if (index == currentItem) {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxHeight()
                            .align(Alignment.Center)
                            .graphicsLayer {
                                alpha = foregroundAlpha

                                val scale =
                                    0.95f + (foregroundAlpha * 0.05f)

                                scaleX = scale
                                scaleY = scale

                                clip = true
                                shape = clipShape
                            },
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
    }
}