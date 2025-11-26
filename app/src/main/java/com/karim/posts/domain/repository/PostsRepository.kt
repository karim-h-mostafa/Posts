package com.karim.posts.domain.repository

import androidx.paging.PagingData
import com.karim.posts.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {
    fun getPosts(): Flow<PagingData<Post>>
    suspend fun getPost(id: Int): Result<Post>
}