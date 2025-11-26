package com.karim.posts.di

import com.karim.posts.data.datasource.PostsLocalDataSource
import com.karim.posts.data.datasource.PostsLocalDataSourceImpl
import com.karim.posts.data.datasource.PostsRemoteDataSource
import com.karim.posts.data.datasource.PostsRemoteDataSourceImpl
import com.karim.posts.data.repository.PostsRepositoryImpl
import com.karim.posts.domain.repository.PostsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindPostsRepository(
        postsRepositoryImpl: PostsRepositoryImpl
    ): PostsRepository

    @Binds
    abstract fun bindPostsRemoteDataSource(
        postsRemoteDataSourceImpl: PostsRemoteDataSourceImpl
    ): PostsRemoteDataSource

    @Binds
    abstract fun bindPostsLocalDataSource(
        postsLocalDataSourceImpl: PostsLocalDataSourceImpl
    ): PostsLocalDataSource


}