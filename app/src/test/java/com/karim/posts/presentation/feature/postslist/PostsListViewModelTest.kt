package com.karim.posts.presentation.feature.postslist

import androidx.paging.PagingData
import com.karim.posts.domain.model.Post
import com.karim.posts.domain.usecase.GetPostsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test

class PostsListViewModelTest {
    private lateinit var getPostsUseCase: GetPostsUseCase
    private lateinit var viewModel: PostsListViewModel

    @Before
    fun setup() {
        getPostsUseCase = mockk()
    }

    private fun createViewModel(): PostsListViewModel {
        return PostsListViewModel(getPostsUseCase)
    }

    @Test
    fun `postsState should emit paging data from use case`() = runTest {
        // Given
        val expectedPagingData = PagingData.empty<Post>()
        every { getPostsUseCase() } returns flowOf(expectedPagingData)

        // When
        viewModel = createViewModel()

        // Then
        viewModel.postsState.test {
            assertEquals(expectedPagingData, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `postsState should emit multiple paging data items`() = runTest {
        // Given
        val firstPagingData = PagingData.empty<Post>()
        val secondPagingData = PagingData.empty<Post>()
        every { getPostsUseCase() } returns flowOf(firstPagingData, secondPagingData)

        // When
        viewModel = createViewModel()

        // Then
        viewModel.postsState.test {
            assertEquals(firstPagingData, awaitItem())
            assertEquals(secondPagingData, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `postIntent ClickPost should emit NavigateToPostDetails effect`() = runTest {
        // Given
        val postId = 1
        val expectedPagingData = PagingData.empty<Post>()
        every { getPostsUseCase() } returns flowOf(expectedPagingData)
        viewModel = createViewModel()

        // When & Then
        viewModel.effect.test {
            viewModel.postIntent(PostsIntent.ClickPost(postId))

            val effect = awaitItem()
            assertEquals(PostsEffect.NavigateToPostDetails(postId), effect)
        }
    }

    @Test
    fun `postIntent ClickPost should emit effect with correct post id`() = runTest {
        // Given
        val postId = 123
        val expectedPagingData = PagingData.empty<Post>()
        every { getPostsUseCase() } returns flowOf(expectedPagingData)
        viewModel = createViewModel()

        // When & Then
        viewModel.effect.test {
            viewModel.postIntent(PostsIntent.ClickPost(postId))

            val effect = awaitItem()
            assertTrue(effect is PostsEffect.NavigateToPostDetails)
            assertEquals(postId, (effect as PostsEffect.NavigateToPostDetails).id)
        }
    }

    @Test
    fun `postIntent multiple ClickPost should emit multiple NavigateToPostDetails effects`() = runTest {
        // Given
        val firstPostId = 1
        val secondPostId = 2
        val expectedPagingData = PagingData.empty<Post>()
        every { getPostsUseCase() } returns flowOf(expectedPagingData)
        viewModel = createViewModel()

        // When & Then
        viewModel.effect.test {
            viewModel.postIntent(PostsIntent.ClickPost(firstPostId))
            assertEquals(PostsEffect.NavigateToPostDetails(firstPostId), awaitItem())

            viewModel.postIntent(PostsIntent.ClickPost(secondPostId))
            assertEquals(PostsEffect.NavigateToPostDetails(secondPostId), awaitItem())
        }
    }
}