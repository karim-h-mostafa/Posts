package com.karim.posts.presentation.feature.postdetails

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.karim.posts.domain.model.Post
import com.karim.posts.domain.usecase.GetPostDetailsUseCase
import com.karim.posts.common.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class PostDetailsViewModelTest {

    private lateinit var getPostDetailsUseCase: GetPostDetailsUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: PostDetailsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getPostDetailsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createSavedStateHandleWithPostId(postId: Int): SavedStateHandle {
        // The key format must match what toRoute() expects
        // For Navigation Compose with type safety, it typically stores under specific keys
        return SavedStateHandle().apply {
            set("id", postId)
        }
    }

    @Test
    fun `initial state should load post successfully`() = runTest {
        // Given
        val postId = 1
        val expectedPost = createTestPost(postId)
        savedStateHandle = createSavedStateHandleWithPostId(postId)
        coEvery { getPostDetailsUseCase(postId) } returns Result.Success(expectedPost)

        // When
        viewModel = PostDetailsViewModel(getPostDetailsUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(postId, state.id)
            assertEquals(expectedPost, state.post)
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `getPostDetails should update state with success result`() = runTest {
        // Given
        val postId = 1
        val expectedPost = createTestPost(postId)
        savedStateHandle = createSavedStateHandleWithPostId(postId)
        coEvery { getPostDetailsUseCase(postId) } returns Result.Success(expectedPost)

        // When
        viewModel = PostDetailsViewModel(getPostDetailsUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val successState = awaitItem()
            assertEquals(expectedPost, successState.post)
            assertEquals(postId, successState.id)
            assertFalse(successState.isLoading)
            assertNull(successState.errorMessage)
        }

        coVerify(exactly = 1) { getPostDetailsUseCase(postId) }
    }

    @Test
    fun `getPostDetails should update state with error result`() = runTest {
        // Given
        val postId = 1
        val errorMessage = "Network error"
        val exception = Exception(errorMessage)
        savedStateHandle = createSavedStateHandleWithPostId(postId)
        coEvery { getPostDetailsUseCase(postId) } returns Result.Error(exception)

        // When
        viewModel = PostDetailsViewModel(getPostDetailsUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val errorState = awaitItem()
            assertEquals(postId, errorState.id)
            assertNull(errorState.post)
            assertFalse(errorState.isLoading)
            assertEquals(errorMessage, errorState.errorMessage)
        }
    }

    @Test
    fun `getPostDetails should handle loading state`() = runTest {
        // Given
        val postId = 1
        savedStateHandle = createSavedStateHandleWithPostId(postId)
        coEvery { getPostDetailsUseCase(postId) } returns Result.Loading

        // When
        viewModel = PostDetailsViewModel(getPostDetailsUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val loadingState = awaitItem()
            assertEquals(postId, loadingState.id)
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.errorMessage)
        }
    }

    @Test
    fun `postIntent Retry should call getPostDetails again and update state`() = runTest {
        // Given
        val postId = 1
        val expectedPost = createTestPost(postId)
        savedStateHandle = createSavedStateHandleWithPostId(postId)
        coEvery { getPostDetailsUseCase(postId) } returns Result.Success(expectedPost)

        viewModel = PostDetailsViewModel(getPostDetailsUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - Trigger retry
        viewModel.postIntent(PostDetailsIntent.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { getPostDetailsUseCase(postId) }

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedPost, state.post)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `postIntent ClickBack should emit NavigateBack effect`() = runTest {
        // Given
        val postId = 1
        savedStateHandle = createSavedStateHandleWithPostId(postId)
        coEvery { getPostDetailsUseCase(postId) } returns Result.Success(createTestPost(postId))

        viewModel = PostDetailsViewModel(getPostDetailsUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()



        // When & Then
        viewModel.effect.test {
            viewModel.postIntent(PostDetailsIntent.ClickBack)
            val effect = awaitItem()
            assertEquals(PostDetailsEffect.NavigateBack, effect)
        }
    }

    @Test
    fun `retry after error should update state successfully`() = runTest {
        // Given
        val postId = 1
        val expectedPost = createTestPost(postId)
        val exception = Exception("Network error")
        savedStateHandle = createSavedStateHandleWithPostId(postId)

        // First call returns error, second call returns success
        coEvery { getPostDetailsUseCase(postId) } returnsMany listOf(
            Result.Error(exception),
            Result.Success(expectedPost)
        )

        viewModel = PostDetailsViewModel(getPostDetailsUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify error state first
        viewModel.state.test {
            val errorState = awaitItem()
            assertNotNull(errorState.errorMessage)
            expectNoEvents()
        }

        // When - Retry
        viewModel.postIntent(PostDetailsIntent.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Should have success state
        viewModel.state.test {
            val successState = awaitItem()
            assertEquals(expectedPost, successState.post)
            assertFalse(successState.isLoading)
            assertNull(successState.errorMessage)
        }

        coVerify(exactly = 2) { getPostDetailsUseCase(postId) }
    }

    private fun createTestPost(id: Int) = Post(
        id = id,
        title = "Test Title $id",
        imageUrl = "https://example.com/image$id.jpg"
    )
}