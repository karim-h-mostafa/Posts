package com.karim.posts.data.datasource

import com.karim.posts.data.remote.model.PostDTO
import com.karim.posts.data.remote.service.PostApi
import javax.inject.Inject

class PostsRemoteDataSourceImpl @Inject constructor(
    private val postAPI: PostApi,
) : PostsRemoteDataSource {
    override suspend fun getPosts(): List<PostDTO> =
        postAPI.getPosts(1, 20)


}