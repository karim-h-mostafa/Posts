package com.karim.posts.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.karim.posts.common.Result
import com.karim.posts.data.datasource.PostsLocalDataSource
import com.karim.posts.data.mapper.toPost
import com.karim.posts.data.paging.PostRemoteMediator
import com.karim.posts.domain.model.Post
import com.karim.posts.domain.repository.PostsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostsRepositoryImpl @Inject constructor(
    private val localPosts: PostsLocalDataSource,
    private val postRemoteMediator: PostRemoteMediator
) : PostsRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getPosts() : Flow<PagingData<Post>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5,
            enablePlaceholders = false,
        ),
        remoteMediator = postRemoteMediator,
        pagingSourceFactory = { localPosts.getPosts() }
    ).flow.map { pagingData ->
        pagingData.map { it.toPost() }
    }


    override suspend fun getPostDetails(id: Int): Result<Post> = withContext(Dispatchers.IO) {
        Result.Loading
        try {
            val post = localPosts.getPostDetails(id)
            if (post != null) {
                Result.Success(post.toPost())
            } else {
                Result.Error(Exception("Post not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}