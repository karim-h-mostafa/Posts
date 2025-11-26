package com.karim.posts.domain.usecase

import com.karim.posts.domain.model.Post
import com.karim.posts.domain.repository.PostsRepository
import javax.inject.Inject

class GetPostDetailsUseCase @Inject constructor(
    private val postsRepository: PostsRepository
) {
    suspend operator fun invoke(id: Int): Result<Post> = postsRepository.getPost(id)
}