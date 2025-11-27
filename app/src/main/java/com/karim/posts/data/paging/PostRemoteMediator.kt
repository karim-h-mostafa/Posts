package com.karim.posts.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.karim.posts.data.datasource.PostsRemoteDataSource
import com.karim.posts.data.local.PostsRoomDB
import com.karim.posts.data.local.entity.PostEntity
import com.karim.posts.data.mapper.toPostEntity
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val remotePosts: PostsRemoteDataSource,
    private val postsRoomDB: PostsRoomDB
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        return try {
//            The API is not paginated, so no the other load types ex(APPEND, PREPEND) are not needed .
            if (loadType == LoadType.REFRESH) {
                val remotePosts = remotePosts.getPosts()
                val entities = remotePosts.map { it.toPostEntity() }

                postsRoomDB.withTransaction {
                    postsRoomDB.postDao().clearAll()
                    postsRoomDB.postDao().upsertAll(entities)
                }
            }
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }
}