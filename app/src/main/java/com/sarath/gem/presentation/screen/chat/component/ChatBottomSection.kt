package com.sarath.gem.presentation.screen.chat.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun ChatBottomSection(
    modifier: Modifier = Modifier,
    textFieldState: String,
    isLoading: Boolean,
    onTextFieldChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Top) {
        TextField(
            modifier = Modifier.weight(1f),
            value = textFieldState,
            onValueChange = { onTextFieldChange(it) },
            singleLine = false,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions { onSubmit() },
            placeholder = { Text(text = "Ask AI something...") },
            shape = MaterialTheme.shapes.medium,
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                ),
        )

        val bgColor by
            animateColorAsState(
                targetValue =
                    if (isLoading) MaterialTheme.colorScheme.primaryContainer.copy(0.35f)
                    else MaterialTheme.colorScheme.primaryContainer,
                label = "Background Color",
            )

        IconButton(
            colors =
                IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.35f),
                ),
            enabled = !isLoading,
            onClick = { onSubmit() },
        ) {
            AnimatedContent(targetState = isLoading, label = "Loading") { state ->
                if (state) {
                    CircularProgressIndicator(modifier = Modifier.scale(0.75f))
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        }
    }
}
