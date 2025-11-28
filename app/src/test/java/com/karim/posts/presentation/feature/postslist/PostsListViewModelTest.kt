package com.karim.posts.presentation.feature.postslist

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import app.cash.turbine.test
import com.karim.posts.domain.model.Post
import com.karim.posts.domain.usecase.GetPostsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostsListViewModelTest {
    private lateinit var getPostsUseCase: GetPostsUseCase
    private lateinit var viewModel: PostsListViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val differ = AsyncPagingDataDiffer(
        diffCallback = PostDiff,
        updateCallback = NoopListCallback(),
        workerDispatcher = Dispatchers.Main
    )


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getPostsUseCase = mockk()

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    private fun createViewModel(): PostsListViewModel {
        return PostsListViewModel(getPostsUseCase)
    }

    @Test
    fun `postsState should emit empty paging data when no posts`() = runTest {
        // Given
        val emptyPagingData = PagingData.empty<Post>()
        every { getPostsUseCase() } returns flowOf(emptyPagingData)

        // When
        viewModel = createViewModel()

        // Then
        val job = launch {
            viewModel.postsState.collect { pagingData ->
                differ.submitData(pagingData)
            }
        }

        advanceUntilIdle()

        assertEquals(0, differ.itemCount)

        job.cancel()
    }

    @Test
    fun `postsState should emit paging data with posts`() = runTest {
        // Given
        val testPosts = listOf(
            createTestPost(1),
            createTestPost(2),
            createTestPost(3)
        )
        val pagingData = PagingData.from(testPosts)
        every { getPostsUseCase() } returns flowOf(pagingData)

        // When
        viewModel = createViewModel()

        // Then
        val job = launch {
            viewModel.postsState.collect { pagingData ->
                differ.submitData(pagingData)
            }
        }

        advanceUntilIdle() // important

        assertEquals(3, differ.itemCount)
        assertEquals(testPosts[0], differ.getItem(0))
        assertEquals(testPosts[1], differ.getItem(1))
        assertEquals(testPosts[2], differ.getItem(2))

        job.cancel()
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
    fun `postIntent multiple ClickPost should emit multiple NavigateToPostDetails effects`() =
        runTest {
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

    private fun createTestPost(id: Int) = Post(
        id = id,
        title = "Test Title $id",
        imageUrl = "https://example.com/image$id.jpg"
    )
    object PostDiff : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem == newItem
    }
    class NoopListCallback : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}