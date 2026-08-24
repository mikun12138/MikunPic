package me.mikun.mikunpic.component.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AcrylicCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    content: @Composable ColumnScope.() -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = MaterialTheme.shapes.extraLarge

    Card(
        modifier = modifier
            .clickable {
                onClick()
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorScheme.surface.copy(alpha = 0.5f),
                        colorScheme.surfaceContainer.copy(alpha = 0.58f),
                    ),
                ),
                shape = shape,
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(),
        border = BorderStroke(
            width = 1.dp,
            color = colorScheme.outlineVariant.copy(alpha = 0.55f),
        ),
        content = content,
    )
}
