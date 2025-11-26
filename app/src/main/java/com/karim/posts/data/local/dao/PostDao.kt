package com.karim.posts.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.karim.posts.data.local.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM posts")
    fun getPosts(): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getPostItem(id: Int): PostEntity?

    @Upsert
    suspend fun upsertAll(posts: List<PostEntity>)

    @Query("DELETE FROM posts")
    suspend fun clearAll()

}