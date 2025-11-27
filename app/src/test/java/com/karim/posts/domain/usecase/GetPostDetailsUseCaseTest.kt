package com.karim.posts.domain.usecase

import com.karim.posts.common.Result
import com.karim.posts.domain.model.Post
import com.karim.posts.domain.repository.PostsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPostDetailsUseCaseTest {

    private lateinit var repository: PostsRepository
    private lateinit var useCase: GetPostDetailsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPostDetailsUseCase(repository)
    }

    @Test
    fun `invoke should return success result when repository returns success`() = runTest {
        // Given
        val postId = 1
        val expectedPost = Post(id = postId, title = "Test Title", imageUrl = "https://example.com/image.jpg")
        coEvery { repository.getPostDetails(postId) } returns Result.Success(expectedPost)

        // When
        val result = useCase(postId)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedPost, (result as Result.Success).data)
    }

    @Test
    fun `invoke should return error result when repository returns error`() = runTest {
        // Given
        val postId = 1
        val exception = Exception("Post not found")
        coEvery { repository.getPostDetails(postId) } returns Result.Error(exception)

        // When
        val result = useCase(postId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Post not found", (result as Result.Error).exception.message)
    }

    @Test
    fun `invoke should call repository getPostDetails with correct id`() = runTest {
        // Given
        val postId = 1
        val expectedPost = Post(id = postId, title = "Test Title", imageUrl = "https://example.com/image.jpg")
        coEvery { repository.getPostDetails(postId) } returns Result.Success(expectedPost)

        // When
        useCase(postId)

        // Then
        coVerify(exactly = 1) { repository.getPostDetails(postId) }
    }
}

