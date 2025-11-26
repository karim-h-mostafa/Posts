package com.karim.posts.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.karim.posts.data.local.dao.PostDao
import com.karim.posts.data.local.entity.PostEntity


@Database(entities = [PostEntity::class], version = 1)
abstract class PostsRoomDB : RoomDatabase() {
    abstract fun postDao(): PostDao
}
