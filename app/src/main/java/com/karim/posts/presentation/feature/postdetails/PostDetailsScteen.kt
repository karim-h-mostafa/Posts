package com.karim.posts.presentation.feature.postdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karim.posts.R
import com.karim.posts.designsystem.ErrorMessage
import com.karim.posts.designsystem.ImageLoader
import com.karim.posts.designsystem.shimmerEffect
import com.karim.posts.designsystem.theme.PostsTheme
import com.karim.posts.domain.model.Post

@Composable
fun PostDetailsScreen(
    postDetailsViewModel: PostDetailsViewModel = hiltViewModel(),
    sideEffect: (PostDetailsEffect) -> Unit,
) {
    val currentSideEffect by rememberUpdatedState(sideEffect)
    LaunchedEffect(Unit) {
        postDetailsViewModel.effect.collect { currentSideEffect(it) }

    }
    val state by postDetailsViewModel.state.collectAsStateWithLifecycle()

    state.post?.let {
        PostDetailsContent(
            post = it,
            isLoading = state.isLoading,
            onIntent = postDetailsViewModel::postIntent
        )
    } ?: ErrorMessage(
        modifier = Modifier.fillMaxSize(),
        message = state.errorMessage ?: stringResource(id = R.string.something_went_wrong),
        onRetry = { postDetailsViewModel.postIntent(PostDetailsIntent.Retry) }
    )


}

@Composable
fun PostDetailsContent(
    modifier: Modifier = Modifier,
    post: Post,
    isLoading: Boolean = false,
    onIntent: (PostDetailsIntent) -> Unit
) {
    Column(modifier = modifier) {
        IconButton(onClick = { onIntent(PostDetailsIntent.ClickBack) }) {
            Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = stringResource(id = R.string.back)
            )
        }

        ElevatedCard(
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.spacing_medium),
                    vertical = dimensionResource(R.dimen.spacing_large)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(R.dimen.spacing_medium),
                        vertical = dimensionResource(R.dimen.spacing_large)
                    )
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shimmerEffect(isLoading)
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                ImageLoader(
                    data = post.imageUrl,
                    contentDescription = post.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostDetailsContentPreview() {
    PostsTheme {
        PostDetailsContent(
            modifier = Modifier.fillMaxSize(), post = Post(
                id = 1,
                title = "sdasda",
                imageUrl = "https://picsum.photos/200"
            )
        ) {

        }
    }
}