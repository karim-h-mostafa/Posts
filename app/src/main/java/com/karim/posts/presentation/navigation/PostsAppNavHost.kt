package com.karim.posts.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.karim.posts.presentation.feature.postdetails.PostDetailsEffect
import com.karim.posts.presentation.feature.postdetails.PostDetailsScreen
import com.karim.posts.presentation.feature.postslist.PostsEffect
import com.karim.posts.presentation.feature.postslist.PostsListScreen

@Composable
fun PostsAppNavHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    onErrorMessage: (String) -> Unit
) {
    val currentOnErrorMessage by rememberUpdatedState(onErrorMessage)
    NavHost(navController = navHostController, startDestination = Posts, modifier = modifier) {
        composable<Posts> {
            val sideEffect: (PostsEffect) -> Unit = remember {
                { uiEffect ->
                    when (uiEffect) {
                        is PostsEffect.NavigateToPostDetails -> navHostController.navigate(
                            PostDetails(
                                uiEffect.id
                            )
                        )

                        is PostsEffect.ShowErrorMessage -> currentOnErrorMessage(uiEffect.message)
                    }
                }
            }
            PostsListScreen(sideEffect = sideEffect)
        }
        composable<PostDetails> {

            val sideEffect: (PostDetailsEffect) -> Unit = remember {
                { uiEffect: PostDetailsEffect ->
                    when (uiEffect) {
                        is PostDetailsEffect.NavigateBack -> navHostController.popBackStack()
                        is PostDetailsEffect.ShowErrorMessage -> currentOnErrorMessage(uiEffect.message)
                    }
                }
            }
            PostDetailsScreen(sideEffect = sideEffect)
        }
    }
}
