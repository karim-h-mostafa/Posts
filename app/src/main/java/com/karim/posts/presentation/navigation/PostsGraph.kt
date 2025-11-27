package com.karim.posts.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object Posts

@Serializable
data class PostDetails(
    val id: Int,
)