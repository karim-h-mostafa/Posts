package com.karim.posts.presentation.feature.postslist

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.karim.posts.R
import com.karim.posts.common.Constants
import com.karim.posts.designsystem.ErrorMessage
import com.karim.posts.designsystem.shimmerEffect
import com.karim.posts.domain.model.Post

@Composable
fun PostsListScreen(
    postsListViewModel: PostsListViewModel = hiltViewModel(),
    sideEffect: (PostsEffect) -> Unit,
) {
    val posts = postsListViewModel.postsState.collectAsLazyPagingItems()
    val currentSideEffect by rememberUpdatedState(sideEffect)
    LaunchedEffect(Unit) {
        postsListViewModel.effect.collect { effect ->
            currentSideEffect(effect)
        }
    }
    val refresh = posts.loadState.refresh
    val errorMessage = stringResource(R.string.something_went_wrong)
    LaunchedEffect(refresh) {
        if (refresh is LoadState.Error) {
            sideEffect(
                PostsEffect.ShowErrorMessage(
                    message = refresh.error.message.orEmpty().ifEmpty { errorMessage }
                )
            )
        }
    }

    PostsListContent(
        posts = posts,
        modifier = Modifier.fillMaxSize(),
        onIntent = postsListViewModel::postIntent
    )

}

@Composable
private fun PostsListContent(
    modifier: Modifier = Modifier,
    posts: LazyPagingItems<Post>,
    onIntent: (PostsIntent) -> Unit,
) {
    val refresh = posts.loadState.refresh
    when {
        refresh is LoadState.Loading -> {
            val minHeight: Dp = with(LocalDensity.current) {
                (MaterialTheme.typography.titleMedium.lineHeight * 3).toDp()
            }
            LazyColumn(
                modifier,
                contentPadding = PaddingValues(dimensionResource(R.dimen.spacing_medium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
            ) {
                items(10) {
                    LoadingItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                min = minHeight
                            )
                    )
                }
            }
        }

        refresh is LoadState.Error && posts.itemCount == 0 -> {
            ErrorMessage(
                message = "Failed to load posts",
                onRetry = { posts.retry() },
                modifier = modifier
            )
        }

        else -> {
            PagedPostsList(posts, onIntent)

        }
    }


}

@Composable
private fun PagedPostsList(
    posts: LazyPagingItems<Post>,
    onIntent: (PostsIntent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(dimensionResource(R.dimen.spacing_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
    ) {
        items(
            count = posts.itemCount,
            key = { index -> posts[index]?.id ?: index }
        ) { index ->
            posts[index]?.let { post ->
                PostItem(
                    post = post,
                    onClick = { onIntent(PostsIntent.ClickPost(post.id)) }
                )
            } ?: LoadingItem()
        }

        when (posts.loadState.append) {
            is LoadState.Loading -> {
                item {
                    LoadingItem()
                }
            }

            is LoadState.Error -> {
                item {
                    ErrorMessage(
                        modifier = Modifier.fillMaxWidth(),
                        message = "Error loading more posts",
                        onRetry = { posts.retry() }
                    )
                }
            }

            else -> Unit
        }
    }
}


@Composable
private fun LoadingItem(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shimmerEffect(),
    )
}

@Composable
private fun PostItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    post: Post,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_medium)),
        ) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

