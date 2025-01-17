package com.sarath.gem.presentation.screen.chat

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
) {

    val scope = rememberCoroutineScope()

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            item {
                Row(
                    modifier =
                        Modifier.clip(MaterialTheme.shapes.medium)
                            .clickable {
                                onNewChat()
                                scope.launch {
                                    if (drawerState.isOpen) {
                                        drawerState.close()
                                    }
                                }
                            }
                            .background(
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp).copy(0.5f),
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
                val color by
                    animateColorAsState(
                        targetValue =
                            if (it.id == selectedChatId) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                    )
                val textColor by
                    animateColorAsState(
                        targetValue =
                            if (it.id == selectedChatId) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onSurface
                    )

                Row(
                    modifier =
                        Modifier.clip(MaterialTheme.shapes.medium)
                            .clickable {
                                onSetChatId(it.id)
                                scope.launch {
                                    if (drawerState.isOpen) {
                                        drawerState.close()
                                    }
                                }
                            }
                            .background(color = color, shape = MaterialTheme.shapes.medium)
                            .padding(4.dp)
                            .fillMaxWidth()
                            .padding(4.dp),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(
                        text = it.title ?: "New Chat",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = textColor),
                    )
                }
            }
        }
    }
}
