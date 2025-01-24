package com.sarath.gem.presentation.screen.chat

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.ramcosta.composedestinations.annotation.Destination
import com.sarath.gem.core.base.collectState
import com.sarath.gem.core.util.SystemBarsColors
import com.sarath.gem.data.local.model.MessageStatus
import com.sarath.gem.data.local.model.Participant
import com.sarath.gem.navigation.graph.ChatGraph
import com.sarath.gem.presentation.screen.chat.component.ChatBottomSection
import com.sarath.gem.presentation.screen.chat.component.ChatBubble
import com.sarath.gem.presentation.screen.chat.component.ChatStartup
import com.sarath.gem.presentation.screen.chat.viewmodel.ChatUIAction
import com.sarath.gem.presentation.screen.chat.viewmodel.ChatUIState
import com.sarath.gem.presentation.screen.chat.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Destination<ChatGraph>(start = true)
@Composable
fun Chat(viewModel: ChatViewModel = hiltViewModel()) {

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val configuration = LocalConfiguration.current

    val screenWidth by remember { derivedStateOf { configuration.screenWidthDp } }
    val offsetValue by remember { derivedStateOf { (screenWidth * 0.85).dp } }

    // Animation for chat screen sliding out
    val animatedOffset by
        animateDpAsState(
            targetValue = if (drawerState.isOpen) offsetValue else 0.dp,
            animationSpec = tween(600),
            label = "Animated Offset",
        )
    // Animation for drawer sliding in
    val drawerOffset by
        animateDpAsState(
            targetValue = if (drawerState.isOpen) 0.dp else (-offsetValue), // Drawer slides in from left
            animationSpec = tween(600),
            label = "Drawer Offset",
        )

    val animateIcon by
        animateFloatAsState(
            targetValue = (animatedOffset / offsetValue).coerceIn(0f, 1f) * 90f,
            label = "Animated Icon",
        )

    val state by viewModel.collectState()

    SystemBarsColors(navigationBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))

    Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).fillMaxSize()) {
        ChatSideNav(
            modifier =
                Modifier.offset(x = drawerOffset)
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = 0.85f)
                    .padding(horizontal = 3.dp, vertical = 16.dp),
            chats = state.chats,
            selectedChatId = state.chatId,
            drawerState = drawerState,
            onNewChat = { viewModel.onAction(ChatUIAction.CreateNewChat) },
            onSetChatId = { viewModel.onAction(ChatUIAction.SetChatId(it)) },
        )

        Column(
            modifier =
                Modifier.offset(x = animatedOffset).background(MaterialTheme.colorScheme.surface).clickable(
                    enabled = drawerState.isOpen
                ) {
                    scope.launch { drawerState.close() }
                },
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterStart).rotate(animateIcon),
                    onClick = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    },
                ) {
                    Icon(Icons.Rounded.Menu, contentDescription = "Menu")
                }
                Text(text = "GemAI", fontWeight = FontWeight.Bold)
            }

            Content(screenWidth = screenWidth, state = state, onActonEvent = viewModel::onAction)
        }
    }

    BackHandler(enabled = drawerState.isOpen) { scope.launch { drawerState.close() } }
}

@Composable
private fun Content(screenWidth: Int, state: ChatUIState, onActonEvent: (ChatUIAction) -> Unit) {

    val chatBubbleFraction by remember { derivedStateOf { (screenWidth * 0.7).dp } }

    val lazyColumnState = rememberLazyListState()
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // TODO: Fix the colors
        val color = remember(isDarkTheme) {
            if (isDarkTheme) {
                DefaultMarkdownColors(
                    text = Color(0xFFE6E1E5), // Light gray text on dark background
                    codeText = Color(0xFFD8D8D8), // Light gray for code text
                    inlineCodeText = Color(0xFFEDEDED), // Very light gray for inline code
                    linkText = Color(0xFF64B5F6), // Soft blue for links
                    codeBackground = Color(0xFF2C2C2C), // Dark gray background for code blocks
                    inlineCodeBackground = Color(0xFF333333), // Slightly darker background for inline code
                    dividerColor = Color(0xFF444444), // Medium gray for dividers
                )
            } else {
                DefaultMarkdownColors(
                    text = Color(0xFF1C1B1F), // Dark text on light background
                    codeText = Color(0xFF37474F), // Dark gray for code text
                    inlineCodeText = Color(0xFF455A64), // Slightly darker gray for inline code
                    linkText = Color(0xFF1E88E5), // Vivid blue for links
                    codeBackground = Color(0xFFF5F5F5), // Light gray background for code blocks
                    inlineCodeBackground = Color(0xFFEDEDED), // Very light gray background for inline code
                    dividerColor = Color(0xFFDDDDDD), // Light gray for dividers
                )
            }
        }

        // TODO: Fix the typography
        val typography = remember(isDarkTheme) {
            if (isDarkTheme) {
                DefaultMarkdownTypography(
                    text =
                        TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFFE6E1E5), // Light gray text on dark background
                        ),
                    code =
                        TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace, // Monospace font for code
                            color = Color(0xFFD8D8D8), // Light gray for code text
                        ),
                    inlineCode =
                        TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFEDEDED), // Very light gray for inline code
                        ),
                    h1 =
                        TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 34.sp,
                            color = Color(0xFFE6E1E5), // Light gray
                        ),
                    h2 =
                        TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 30.sp,
                            color = Color(0xFFE6E1E5),
                        ),
                    h3 =
                        TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 26.sp,
                            color = Color(0xFFE6E1E5),
                        ),
                    h4 =
                        TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 24.sp,
                            color = Color(0xFFE6E1E5),
                        ),
                    h5 =
                        TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp,
                            color = Color(0xFFE6E1E5),
                        ),
                    h6 =
                        TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp,
                            color = Color(0xFFE6E1E5),
                        ),
                    quote =
                        TextStyle(
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFFB0BEC5), // Subtle gray
                        ),
                    paragraph = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, color = Color(0xFFE6E1E5)),
                    ordered = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, color = Color(0xFFE6E1E5)),
                    bullet = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, color = Color(0xFFE6E1E5)),
                    list = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, color = Color(0xFFE6E1E5)),
                    link =
                        TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textDecoration = TextDecoration.Underline,
                            color = Color(0xFF64B5F6), // Soft blue
                        ),
                )
            } else {
                DefaultMarkdownTypography(
                    text =
                        TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF1C1B1F), // Dark text on light background
                        ),
                    code =
                        TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF37474F), // Dark gray for code text
                        ),
                    inlineCode =
                        TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF455A64), // Slightly darker gray for inline code
                        ),
                    h1 =
                        TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 34.sp,
                            color = Color(0xFF1C1B1F), // Dark text
                        ),
                    h2 =
                        TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 30.sp,
                            color = Color(0xFF1C1B1F),
                        ),
                    h3 =
                        TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 26.sp,
                            color = Color(0xFF1C1B1F),
                        ),
                    h4 =
                        TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 24.sp,
                            color = Color(0xFF1C1B1F),
                        ),
                    h5 =
                        TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp,
                            color = Color(0xFF1C1B1F),
                        ),
                    h6 =
                        TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp,
                            color = Color(0xFF1C1B1F),
                        ),
                    quote =
                        TextStyle(
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF546E7A), // Subtle gray
                        ),
                    paragraph = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, color = Color(0xFF1C1B1F)),
                    ordered = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, color = Color(0xFF1C1B1F)),
                    bullet = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, color = Color(0xFF1C1B1F)),
                    list = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, color = Color(0xFF1C1B1F)),
                    link =
                        TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textDecoration = TextDecoration.Underline,
                            color = Color(0xFF1E88E5), // Vivid blue
                        ),
                )
            }
        }

        LaunchedEffect(state.chat) {
            if (state.chat.isNotEmpty() && lazyColumnState.firstVisibleItemIndex != state.chat.lastIndex) {
                lazyColumnState.animateScrollToItem(state.chat.lastIndex)
            } else if (state.chat.isNotEmpty()) {
                lazyColumnState.animateScrollBy(200f)
            }
        }

        //TODO: Fix the keyboard controller
        val keyBoardController = LocalSoftwareKeyboardController.current

        AnimatedContent(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            targetState = state.chat.isNotEmpty(),
            label = "Chat Content",
        ) { isEmpty ->
            if (isEmpty) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    contentPadding = PaddingValues(vertical = 6.dp, horizontal = 2.dp),
                    state = lazyColumnState,
                ) {
                    items(items = state.chat, key = { it.id }) {
                        val chatBubbleBackgroundColor by
                            animateColorAsState(
                                targetValue =
                                    when (it.participant) {
                                        Participant.USER ->
                                            when (it.status) {
                                                MessageStatus.SENT ->
                                                    MaterialTheme.colorScheme.surfaceColorAtElevation(14.dp)

                                                MessageStatus.LOADING ->
                                                    MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp).copy(0.35f)

                                                MessageStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
                                                else -> MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                                            }

                                        Participant.MODEL -> MaterialTheme.colorScheme.background
                                    },
                                label = "sendBgColor",
                            )

                        Row(
                            modifier = Modifier.padding(5.dp).fillMaxWidth(),
                            horizontalArrangement =
                                if (it.participant == Participant.USER) Arrangement.End else Arrangement.Start,
                        ) {
                            ChatBubble(
                                modifier =
                                    Modifier.background(
                                            color = chatBubbleBackgroundColor,
                                            shape = MaterialTheme.shapes.medium,
                                        )
                                        .then(
                                            if (it.participant == Participant.USER)
                                                Modifier.widthIn(max = chatBubbleFraction)
                                            else Modifier
                                        )
                                        .padding(12.dp),
                                content = it.content,
                                participant = it.participant,
                                color = color,
                                typography = typography,
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(10.dp)) }
                }
            } else {
                val startUps = remember(state.startupPrompts) { state.startupPrompts }
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ChatStartup(
                        modifier = Modifier.align(Alignment.Center).fillMaxWidth(0.65f),
                        startups = startUps,
                        onSelect = {
                            onActonEvent(ChatUIAction.SetPrompt(it))
                            onActonEvent(ChatUIAction.Submit)
                        },
                    )
                }
            }
        }

        ChatBottomSection(
            modifier =
                Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                        shape =
                            RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                    )
                    .height(IntrinsicSize.Max)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 6.dp)
                    .imePadding(),
            textFieldState = state.prompt,
            isLoading = state.isLoading,
            onTextFieldChange = { onActonEvent(ChatUIAction.SetPrompt(it)) },
            onSubmit = {
                onActonEvent(ChatUIAction.Submit)
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatContentPreview() {

    var state by remember {
        mutableStateOf(
            ChatUIState(
                prompt = "",
                chat = emptyList(),
                chats = emptyList(),
                isLoading = false,
                chatId = null,
                startupPrompts = emptyList(),
            )
        )
    }

    Content(
        screenWidth = 600,
        state = state,
        onActonEvent = {
            when (it) {
                ChatUIAction.Clear -> Unit
                ChatUIAction.CreateNewChat -> {}
                is ChatUIAction.SetChatId -> {}
                is ChatUIAction.SetPrompt -> {
                    state = state.copy(prompt = it.prompt)
                }
                ChatUIAction.Submit -> {}
            }
        },
    )
}
