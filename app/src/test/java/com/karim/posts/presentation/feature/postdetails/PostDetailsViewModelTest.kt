package com.karim.posts.presentation.feature.postdetails

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.karim.posts.common.Result
import com.karim.posts.domain.model.Post
import com.karim.posts.domain.usecase.GetPostDetailsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PostDetailsViewModelTest {

    private lateinit var getPostDetailsUseCase: GetPostDetailsUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: PostDetailsViewModel

    @Before
    fun setup() {
        getPostDetailsUseCase = mockk()
    }

    private fun createSavedStateHandleWithPostId(postId: Int): SavedStateHandle {
        // Create a SavedStateHandle with route arguments
        // Note: toRoute() in ViewModel init requires proper navigation setup
        // For unit tests, we use SavedStateHandle with the id parameter
        // The actual toRoute() call may fail in unit tests, but we test StateFlow behavior
        val savedStateHandle = SavedStateHandle(mapOf("id" to postId))
        return savedStateHandle
    }

    @Test
    fun `initial state should have default values and transition to loading`() = runTest {
        // Given
        val postId = 1
        savedStateHandle = createSavedStateHandleWithPostId(postId)
        coEvery { getPostDetailsUseCase(postId) } coAnswers {
            kotlinx.coroutines.delay(100)
            Result.Success(createTestPost(postId))
        }

        // When
        viewModel = PostDetailsViewModel(getPostDetailsUseCase, savedStateHandle)

        // Then - Test StateFlow emissions with Turbine
        viewModel.state.test {
            // First emission: initial state with id set
            val initialState = awaitItem()
            assertEquals(postId, initialState.id)
            assertNull(initialState.post)
            assertTrue(initialState.isLoading)
            assertNull(initialState.errorMessage)
            
            // Second emission: after loading completes
            val loadedState = awaitItem()
            assertEquals(postId, loadedState.id)
            assertNotNull(loadedState.post)
            assertFalse(loadedState.isLoading)
            assertNull(loadedState.errorMessage)
            
            cancelAndIgnoreRemainingEvents()
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

        // Then - Test complete state flow: initial -> loading -> success
        viewModel.state.test {
            // First: Initial state with id
            val initialState = awaitItem()
            assertEquals(postId, initialState.id)
            assertTrue(initialState.isLoading)
            
            // Second: Success state
            val successState = awaitItem()
            assertEquals(expectedPost, successState.post)
            assertEquals(postId, successState.id)
            assertFalse(successState.isLoading)
            assertNull(successState.errorMessage)
            
            cancelAndIgnoreRemainingEvents()
        }
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

        // Then - Test complete state flow: initial -> loading -> error
        viewModel.state.test {
            // First: Initial state with id and loading
            val initialState = awaitItem()
            assertEquals(postId, initialState.id)
            assertTrue(initialState.isLoading)
            
            // Second: Error state
            val errorState = awaitItem()
            assertEquals(postId, errorState.id)
            assertNull(errorState.post)
            assertFalse(errorState.isLoading)
            assertEquals(errorMessage, errorState.errorMessage)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `postIntent Retry should call getPostDetails again and update state`() = runTest {
        // Given
        val postId = 1
        val expectedPost = createTestPost(postId)
        savedStateHandle = createSavedStateHandleWithPostId(postId)
        coEvery { getPostDetailsUseCase(postId) } returns Result.Success(expectedPost)

        // When
        viewModel = PostDetailsViewModel(getPostDetailsUseCase, savedStateHandle)
        
        // Wait for initial load
        viewModel.state.test {
            awaitItem() // initial state
            awaitItem() // loaded state
            cancelAndIgnoreRemainingEvents()
        }
        
        // Trigger retry
        viewModel.postIntent(PostDetailsIntent.Retry)

        // Then - Verify state transitions during retry
        viewModel.state.test {
            // Should transition to loading again
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            // Then back to success
            val successState = awaitItem()
            assertEquals(expectedPost, successState.post)
            assertFalse(successState.isLoading)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        io.mockk.coVerify(atLeast = 2) { getPostDetailsUseCase(postId) }
    }

    @Test
    fun `postIntent ClickBack should emit NavigateBack effect`() = runTest {
        // Given
        val postId = 1
        savedStateHandle = createSavedStateHandleWithPostId(postId)
        coEvery { getPostDetailsUseCase(postId) } returns Result.Success(createTestPost(postId))

        // When
        viewModel = PostDetailsViewModel(getPostDetailsUseCase, savedStateHandle)
        viewModel.postIntent(PostDetailsIntent.ClickBack)

        // Then
        viewModel.effect.test {
            val effect = awaitItem()
            assertEquals(PostDetailsEffect.NavigateBack, effect)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestPost(id: Int) = Post(
        id = id,
        title = "Test Title $id",
        imageUrl = "https://example.com/image$id.jpg"
    )
}

