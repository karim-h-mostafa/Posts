package com.karim.posts.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// @JsonClass(generateAdapter = true) is better for performance as it generates code instead of depending on reflection
@JsonClass(generateAdapter = true)
data class PostDTO(
    @Json(name = "title")
    val title: String,
    @Json(name = "url")
    val imageUrl: String
)
