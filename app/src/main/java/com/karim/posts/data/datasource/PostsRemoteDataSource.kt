package com.karim.posts.data.datasource

import com.karim.posts.data.remote.model.PostDTO

interface PostsRemoteDataSource {
    suspend fun getPosts(): List<PostDTO>
}

