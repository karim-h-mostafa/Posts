package com.karim.posts.data.mapper

import com.karim.posts.data.local.entity.PostEntity
import com.karim.posts.data.remote.model.PostDTO
import com.karim.posts.domain.model.Post


fun PostEntity.toPost(): Post = Post(
    id = id,
    title = title,
    imageUrl = imageUrl
)

fun PostDTO.toPostEntity(): PostEntity = PostEntity(
    title = title,
    imageUrl = imageUrl
)

