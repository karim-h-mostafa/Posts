package com.karim.posts.presentation.feature.postslist

import com.karim.posts.presentation.feature.postdetails.PostDetailsEffect


sealed class PostsIntent {
    data class ClickPost(val id: Int) : PostsIntent()
}

sealed class PostsEffect {
    data class NavigateToPostDetails(val id: Int) : PostsEffect()
    data class ShowErrorMessage(val message: String) : PostsEffect()
}