package com.karim.posts

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class PostsAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val snackBarHostState: SnackbarHostState
) {
    fun showSnackBar(message: String) {
        coroutineScope.launch {
            snackBarHostState.showSnackbar(message)
        }
    }
}

@Composable
fun rememberPostsAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }

): PostsAppState {
    return remember(navController, coroutineScope, snackBarHostState) {
        PostsAppState(navController, coroutineScope, snackBarHostState)
    }
}