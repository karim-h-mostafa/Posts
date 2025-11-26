package com.karim.posts.domain.repository

import androidx.paging.PagingData
import com.karim.posts.common.Result
import com.karim.posts.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {
     fun getPosts(): Flow<PagingData<Post>>
    suspend fun getPostDetails(id: Int): Result<Post>
}