package com.karim.posts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.karim.posts.designsystem.theme.PostsTheme
import com.karim.posts.presentation.navigation.PostsAppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val postsAppState = rememberPostsAppState()
            PostsTheme {
                PostsApp(postsAppState)
            }
        }
    }
}

@Composable
private fun PostsApp(postsAppState: PostsAppState = rememberPostsAppState()) {
    Scaffold(modifier = Modifier.fillMaxSize(), snackbarHost = {
        SnackbarHost(postsAppState.snackBarHostState)
    }) { innerPadding ->
        val onErrorMessage = remember {
            { message: String -> postsAppState.showSnackBar(message) }
        }
        PostsAppNavHost(
            modifier = Modifier.padding(innerPadding),
            navHostController = postsAppState.navController,
            onErrorMessage = onErrorMessage
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PostsAppPreview() {
    PostsTheme {
        PostsApp()
    }
}

