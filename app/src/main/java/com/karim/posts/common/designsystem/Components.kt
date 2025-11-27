package com.karim.posts.common.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.SubcomposeAsyncImage
import com.karim.posts.R

/**
 * Modern ImageLoader using SubcomposeAsyncImage - eliminates manual state tracking
 * and provides cleaner, more performant image loading with built-in state handling
 */
@Composable
fun ImageLoader(
    data: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Int? = null,
    error: Int? = null,
    loading: @Composable (() -> Unit)? = null,
    shape: Shape? = null,
    backgroundColor: Color = Color.Transparent,
    colorFilter: ColorFilter? = null,
    alpha: Float = 1f,
) {
    SubcomposeAsyncImage(
        model = data,
        contentDescription = contentDescription,
        modifier = modifier
            .then(shape?.let { Modifier.clip(it) } ?: Modifier),
        contentScale = contentScale,
        colorFilter = colorFilter,
        alpha = alpha,
        loading = {
            loading?.invoke() ?: placeholder?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(backgroundColor)
                )
            } ?: Box(modifier.shimmerEffect())
        },
        error = {
            error?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(backgroundColor)
                )
            } ?: placeholder?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(backgroundColor)
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.erro_place_holder),
                contentDescription = null,
                modifier = modifier,
            )
        },
    )
}



