package me.mikun.mikunpic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.mikun.mikunpic.component.PicCarousel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val imageUrls = listOf(
            "http://127.0.0.1:8081/random",
            "http://127.0.0.1:8081/random",
            "http://127.0.0.1:8081/random",
        )
        PicCarousel(
            imageUrls
        )
    }
}