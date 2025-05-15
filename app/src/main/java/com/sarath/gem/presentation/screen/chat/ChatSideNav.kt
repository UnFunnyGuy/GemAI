package com.sarath.gem.presentation.screen.chat

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.sarath.gem.R
import com.sarath.gem.domain.model.Conversation
import kotlinx.coroutines.launch

@Composable
fun ChatSideNav(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    chats: List<Conversation>,
    selectedChatId: Long?,
    onNewChat: () -> Unit,
    onSetChatId: (Long) -> Unit,
    deleteChat:(Long)->Unit
) {

    val scope = rememberCoroutineScope()
    var openDeleteDialog by remember {
        mutableStateOf(false)
    }
    var selectedId by remember {
        mutableLongStateOf(0L)
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            item {
                Row(
                    modifier =
                        Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                onNewChat()
                                scope.launch {
                                    if (drawerState.isOpen) {
                                        drawerState.close()
                                    }
                                }
                            }
                            .background(
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                                    .copy(0.5f),
                                shape = MaterialTheme.shapes.medium,
                            )
                            .padding(4.dp)
                            .fillMaxWidth()
                            .padding(4.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "New Chat", tint = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = "New Chat",
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
            items(items = chats, key = { it.id }) {
                item ->
                val color by
                    animateColorAsState(
                        targetValue =
                            if (item.id == selectedChatId) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                    )
                val textColor by
                    animateColorAsState(
                        targetValue =
                            if (item.id == selectedChatId) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onSurface
                    )

                Row(
                    modifier =
                        Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        openDeleteDialog = true
                                        selectedId = item.id
                                    }
                                    ,
                                    onTap = {
                                        onSetChatId(item.id)
                                        scope.launch {
                                            if (drawerState.isOpen) {
                                                drawerState.close()
                                            }
                                        }
                                    }
                                )
                            }
                            .background(color = color, shape = MaterialTheme.shapes.medium)
                            .padding(4.dp)
                            .fillMaxWidth()
                            .padding(4.dp),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(
                        text = item.title ?: "New Chat",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = textColor),
                    )
                }
            }
        }
        if (openDeleteDialog){
            AlertDialog(
                onDismissRequest = {

                },
                title = {
                    Text(text = stringResource(R.string.delete_chat), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface))
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            deleteChat.invoke(selectedId)
                            openDeleteDialog = false
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.confirm_button),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openDeleteDialog = false
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

    }
}
