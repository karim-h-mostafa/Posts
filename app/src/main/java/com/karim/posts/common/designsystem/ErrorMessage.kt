package com.karim.posts.common.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.karim.posts.R


@Composable
fun ErrorMessage(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit,
) {

    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_medium)),
        modifier = modifier
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Column(
                modifier = Modifier
                    .fillMaxWidth().padding(
                        horizontal = dimensionResource(R.dimen.spacing_medium),
                        vertical = dimensionResource(R.dimen.spacing_large)
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.spacing_xlarge))
                )

                TextButton(
                    onClick = onRetry
                ) {
                    Text(text = stringResource(id = R.string.retry))
                }
            }
        }

    }
}

