package com.karim.posts.data.remote.service

import com.karim.posts.data.remote.model.PostDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApi {
    @GET("photos")
    suspend fun getPosts(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<PostDTO>

}