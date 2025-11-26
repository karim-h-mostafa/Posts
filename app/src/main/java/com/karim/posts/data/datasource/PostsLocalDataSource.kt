package com.karim.posts.data.datasource

import androidx.paging.PagingSource
import com.karim.posts.data.local.entity.PostEntity

interface PostsLocalDataSource {
    fun getPosts(): PagingSource<Int, PostEntity>
    suspend fun getPostDetails(id: Int): PostEntity?

    suspend fun clearAll()
    suspend fun insertAll(posts: List<PostEntity>)
}


