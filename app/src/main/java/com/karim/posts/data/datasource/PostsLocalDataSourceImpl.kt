package com.karim.posts.data.datasource

import androidx.paging.PagingSource
import com.karim.posts.data.local.dao.PostDao
import com.karim.posts.data.local.entity.PostEntity
import javax.inject.Inject

class PostsLocalDataSourceImpl @Inject constructor(
    private val postDao: PostDao,
) : PostsLocalDataSource {
    override fun getPosts(): PagingSource<Int, PostEntity> = postDao.getPosts()
    override suspend fun getPostDetails(id: Int): PostEntity? = postDao.getPostItem(id)
    override suspend fun clearAll() = postDao.clearAll()
    override suspend fun insertAll(posts: List<PostEntity>) = postDao.upsertAll(posts)

}