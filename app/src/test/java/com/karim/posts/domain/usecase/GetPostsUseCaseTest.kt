package com.karim.posts.domain.usecase

import androidx.paging.PagingData
import app.cash.turbine.test
import com.karim.posts.domain.model.Post
import com.karim.posts.domain.repository.PostsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPostsUseCaseTest {

    private lateinit var repository: PostsRepository
    private lateinit var useCase: GetPostsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPostsUseCase(repository)
    }

    @Test
    fun `invoke should return flow of paging data from repository`() = runTest {
        // Given
        val expectedPagingData = PagingData.empty<Post>()
        every { repository.getPosts() } returns flowOf(expectedPagingData)

        // When + Then
        useCase().test {
            assertEquals(expectedPagingData, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should call repository getPosts method`() = runTest {
        // Given
        val expectedPagingData = PagingData.empty<Post>()
        every { repository.getPosts() } returns flowOf(expectedPagingData)

        // When
        useCase().test {
            awaitItem()
            awaitComplete()
        }

        // Then
        verify(exactly = 1) { repository.getPosts() }
    }
}