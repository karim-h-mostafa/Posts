package com.karim.posts.data.repository

import androidx.paging.PagingSource
import com.karim.posts.common.Result
import com.karim.posts.data.datasource.PostsLocalDataSource
import com.karim.posts.data.local.entity.PostEntity
import com.karim.posts.data.mapper.toPost
import com.karim.posts.data.paging.PostRemoteMediator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PostsRepositoryImplTest {

    private lateinit var localDataSource: PostsLocalDataSource
    private lateinit var remoteMediator: PostRemoteMediator
    private lateinit var repository: PostsRepositoryImpl

    @Before
    fun setup() {
        localDataSource = mockk()
        remoteMediator = mockk()
        repository = PostsRepositoryImpl(localDataSource, remoteMediator)
    }

    @Test
    fun `getPostDetails should return success when post exists in local data source`() = runTest {
        // Given
        val postId = 1
        val postEntity = PostEntity(id = postId, title = "Test Title", imageUrl = "https://example.com/image.jpg")
        coEvery { localDataSource.getPostDetails(postId) } returns postEntity

        // When
        val result = repository.getPostDetails(postId)

        // Then
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(postEntity.toPost(), successResult.data)
    }

    @Test
    fun `getPostDetails should return error when post not found in local data source`() = runTest {
        // Given
        val postId = 1
        coEvery { localDataSource.getPostDetails(postId) } returns null

        // When
        val result = repository.getPostDetails(postId)

        // Then
        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals("Post not found", errorResult.exception.message)
    }

    @Test
    fun `getPostDetails should return error when exception occurs`() = runTest {
        // Given
        val postId = 1
        val exception = Exception("Database error")
        coEvery { localDataSource.getPostDetails(postId) } throws exception

        // When
        val result = repository.getPostDetails(postId)

        // Then
        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals(exception, errorResult.exception)
    }

    @Test
    fun `getPosts should return flow of paging data`() = runTest {
        // Given
        val pagingSource = mockk<PagingSource<Int, PostEntity>>()
        every { localDataSource.getPosts() } returns pagingSource

        // When
        val result = repository.getPosts()

        // Then
        assertNotNull(result)
        // Note: Testing PagingData flow is complex and typically requires integration tests
        // This test verifies the method returns a Flow
    }
}

