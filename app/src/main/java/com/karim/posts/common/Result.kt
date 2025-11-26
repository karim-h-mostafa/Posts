package com.karim.posts.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class Result<out T> {
    class Success<out T>(val data: T) : Result<T>()
    class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it)) }
}

inline fun <In, Out> Result<In>.map(transform: (In) -> Out) = when (this) {
    is Result.Success -> Result.Success(transform(data))
    else -> this
}
