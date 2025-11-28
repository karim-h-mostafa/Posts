package com.karim.posts.presentation.feature.postdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.karim.posts.common.Result
import com.karim.posts.domain.usecase.GetPostDetailsUseCase
import com.karim.posts.presentation.navigation.PostDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val getPostUseCase: GetPostDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(PostDetailsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PostDetailsEffect>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.asSharedFlow()

    init {
        val postDetails = savedStateHandle.toRoute<PostDetails>()
        postDetails.id.let {
            updateState { copy(id = it) }
            getPostDetails(it)
        }
    }

    private fun getPostDetails(id: Int) = viewModelScope.launch {
        val postDetailsResult = getPostUseCase(id)
        when (postDetailsResult) {
            is Result.Error -> {
                updateState {
                    copy(
                        errorMessage = postDetailsResult.exception.message,
                        isLoading = false
                    )
                }
            }

            Result.Loading -> {
                updateState { copy(isLoading = true, errorMessage = null) }
            }

            is Result.Success -> {
                updateState { copy(post = postDetailsResult.data, isLoading = false, errorMessage = null) }
            }
        }
    }


    fun postIntent(intent: PostDetailsIntent) {
        when (intent) {
            is PostDetailsIntent.ClickBack -> {
                postEffect(PostDetailsEffect.NavigateBack)
            }


            PostDetailsIntent.Retry -> {
                getPostDetails(state.value.id)
            }
        }
    }

    private fun postEffect(effect: PostDetailsEffect) {
        _effect.tryEmit(effect)
    }

    private fun updateState(reducer: PostDetailsState.() -> PostDetailsState) {
        _state.update { postDetailsState -> postDetailsState.reducer() }
    }

}