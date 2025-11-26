package com.karim.posts.domain.usecase

import androidx.paging.PagingData
import com.karim.posts.domain.model.Post
import com.karim.posts.domain.repository.PostsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val postsRepository: PostsRepository
) {
    operator fun invoke(): Flow<PagingData<Post>> = postsRepository.getPosts()
}
