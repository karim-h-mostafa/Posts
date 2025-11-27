package com.karim.posts.presentation.feature.postdetails

import com.karim.posts.domain.model.Post

data class PostDetailsState(
    val id: Int = 0,
    val post: Post? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed class PostDetailsIntent {
    object ClickBack : PostDetailsIntent()
    object Retry : PostDetailsIntent()
}

sealed class PostDetailsEffect {
    object NavigateBack : PostDetailsEffect()
    data class ShowErrorMessage(val message: String) : PostDetailsEffect()
}