package com.karim.posts.di

import android.content.Context
import androidx.room.Room
import com.karim.posts.common.Constants
import com.karim.posts.data.local.PostsRoomDB
import com.karim.posts.data.local.dao.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PostsRoomDB = Room.databaseBuilder(
        context,
        PostsRoomDB::class.java,
        Constants.DATABASE_NAME
    ).build()

    @Provides
    @Singleton
    fun providePostDao(
        postsRoomDB: PostsRoomDB
    ): PostDao = postsRoomDB.postDao()

}