package com.karim.posts.presentation.feature.postslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.karim.posts.domain.model.Post
import com.karim.posts.domain.usecase.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject


@HiltViewModel
class PostsListViewModel @Inject constructor(
    getPostsUseCase: GetPostsUseCase,
) : ViewModel() {

    private val _effect = MutableSharedFlow<PostsEffect>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.asSharedFlow()

    val postsState: Flow<PagingData<Post>> = getPostsUseCase().cachedIn(viewModelScope)


    fun postIntent(intent: PostsIntent) {
        when (intent) {
            is PostsIntent.ClickPost -> {
                postEffect(PostsEffect.NavigateToPostDetails(intent.id))
            }
        }
    }

    private fun postEffect(effect: PostsEffect) {
        _effect.tryEmit(effect)
    }


}
