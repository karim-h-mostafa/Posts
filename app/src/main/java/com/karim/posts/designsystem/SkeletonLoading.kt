package com.karim.posts.designsystem

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

fun Modifier.shimmerEffect(isLoading: Boolean = true, shape: Shape? = null): Modifier =
    if (isLoading) composed {

        // 1. Define the coordinates for the animation
        var size by remember { mutableStateOf(IntSize.Zero) }
        val transition = rememberInfiniteTransition(label = "Shimmer")

        // 2. Animate the start position of the gradient
        val startOffsetX by transition.animateFloat(
            initialValue = -2 * size.width.toFloat(),
            targetValue = 2 * size.width.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(1000), // Duration of the shimmer
                repeatMode = RepeatMode.Restart
            ),
            label = "ShimmerOffset"
        )

        // 3. Define the shimmering colors
        // Adjust these based on your theme (Light/Dark mode)
        val shimmerColors = listOf(
            Color(0xFFB8B5B5), // Base Gray
            Color(0xFF8F8B8B), // Lighter Gray (Highlight)
            Color(0xFFB8B5B5), // Base Gray
        )

        // 4. Create the Linear Gradient Brush
        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )

        // 5. Return the background modifier
        this
            .clip(shape ?: MaterialTheme.shapes.small)
            .background(brush)
            .onGloballyPositioned {
                size = it.size
            }

    } else Modifier