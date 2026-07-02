package me.mikun.mikunpic.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.PicCarousel(
    painters: List<Painter>,
    readyPop: Boolean,
) {
    val actualSize = painters.size

    val carouselState = rememberCarouselState(
        initialItem =
        Int.MAX_VALUE / 2 -
            (Int.MAX_VALUE / 2) % actualSize,
    ) {
        Int.MAX_VALUE
    }

    val bgBlurRadius by animateDpAsState(
        targetValue = if (
            carouselState.isScrollInProgress || !readyPop
        ) {
            0.dp
        } else {
            20.dp
        },

        animationSpec = tween(600),
    )

    val fgAlpha by animateFloatAsState(
        targetValue = if (
            carouselState.isScrollInProgress || !readyPop
        ) {
            0f
        } else {
            1f
        },

        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
    )

    HorizontalCenteredHeroCarousel(
        state = carouselState,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) { index ->
        val realIndex =
            index % actualSize

        fun shouldLoad(index: Int): Boolean = abs(index - carouselState.currentItem) <= 2
        if (shouldLoad(index)) {
            val painter = painters[realIndex]

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .maskClip(MaterialTheme.shapes.extraLarge)
                    .blur(bgBlurRadius),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
            )

            if (index == carouselState.currentItem) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxHeight()
                        .align(Alignment.Center)
                        .graphicsLayer {
                            alpha = fgAlpha

                            val scale =
                                0.95f + (fgAlpha * 0.05f)

                            scaleX = scale
                            scaleY = scale
                        }
                        .maskClip(MaterialTheme.shapes.extraLarge),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}
