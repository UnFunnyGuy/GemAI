package com.sarath.gem.presentation.screen.onboarding

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.navgraphs.MainNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sarath.gem.core.base.SingleEventEffect
import com.sarath.gem.core.base.collectState
import com.sarath.gem.navigation.graph.OnboardingGraph
import com.sarath.gem.presentation.screen.onboarding.viewmodel.ApiKeyCheckStatus
import com.sarath.gem.presentation.screen.onboarding.viewmodel.OnboardingUIAction
import com.sarath.gem.presentation.screen.onboarding.viewmodel.OnboardingUIEvent
import com.sarath.gem.presentation.screen.onboarding.viewmodel.OnboardingUIState
import com.sarath.gem.presentation.screen.onboarding.viewmodel.OnboardingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO : Tablet UI
@Destination<OnboardingGraph>(start = true)
@Composable
fun OnboardingScreen(navigator: DestinationsNavigator, viewModel: OnboardingViewModel = hiltViewModel()) {

    val state by viewModel.collectState()
    val context = LocalContext.current
    val view = LocalView.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            modifier = Modifier.align(Alignment.TopCenter),
            text = "GemAI",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Content(state = state, onAction = viewModel::onAction)
    }

    SingleEventEffect(viewModel.uiEvent) { event: OnboardingUIEvent ->
        when (event) {
            is OnboardingUIEvent.NavigateToChatScreen -> {
                navigator.navigate(MainNavGraph)
            }

            is OnboardingUIEvent.ShowToast -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }

            OnboardingUIEvent.GetApiKey -> {
                try {
                    val activity = view.context as ComponentActivity
                    val intent = Intent(Intent.ACTION_VIEW, "https://aistudio.google.com/app/apikey".toUri())
                    activity.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
private fun Content(state: OnboardingUIState, onAction: (OnboardingUIAction) -> Unit) {
    Column(
        modifier =
            Modifier.imePadding()
                .fillMaxWidth(0.9f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
                    shape = MaterialTheme.shapes.medium,
                )
                .background(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp).copy(0.45f),
                    shape = MaterialTheme.shapes.medium,
                )
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.apiKey,
            onValueChange = { onAction(OnboardingUIAction.SetApiKey(it)) },
            singleLine = false,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions { onAction(OnboardingUIAction.SaveApiKey) },
            placeholder = { Text(text = "Enter API Key") },
            shape = MaterialTheme.shapes.medium,
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                ),
        )
        AnimatedVisibility(
            visible = state.apiKeyState.isNone.not(),
            modifier = Modifier.padding(top = 6.dp).fillMaxWidth(),
        ) {
            val bgColor by
                animateColorAsState(
                    targetValue =
                        when (state.apiKeyState) {
                            ApiKeyCheckStatus.VALID -> Color(0xFF4BB543)
                            ApiKeyCheckStatus.INVALID -> MaterialTheme.colorScheme.error
                            ApiKeyCheckStatus.CHECKING -> MaterialTheme.colorScheme.secondary
                            ApiKeyCheckStatus.NONE -> Color.Transparent
                        }
                )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier =
                        Modifier.background(color = bgColor.copy(0.35f), shape = MaterialTheme.shapes.small)
                            .border(width = 2.dp, color = bgColor.copy(0.9f), shape = MaterialTheme.shapes.small),
                    contentAlignment = Alignment.Center,
                ) {
                    AnimatedContent(targetState = state.apiKeyState, label = "api key state icon") { apiKeyState ->
                        when (apiKeyState) {
                            ApiKeyCheckStatus.CHECKING -> {
                                Icon(
                                    modifier = Modifier.scale(0.75f),
                                    imageVector = Icons.Rounded.HourglassBottom,
                                    contentDescription = "Invalid",
                                    tint = MaterialTheme.colorScheme.secondary,
                                )
                            }

                            ApiKeyCheckStatus.INVALID -> {
                                Icon(
                                    modifier = Modifier.scale(0.75f),
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Invalid",
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }

                            ApiKeyCheckStatus.VALID -> {
                                Icon(
                                    modifier = Modifier.scale(0.75f),
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = "Valid",
                                    tint = Color(0xFF4BB543),
                                )
                            }

                            ApiKeyCheckStatus.NONE -> {}
                        }
                    }
                }
                AnimatedContent(
                    modifier = Modifier.padding(start = 8.dp),
                    targetState = state.apiKeyState,
                    label = "api key state",
                ) { apiKeyState ->
                    when (apiKeyState) {
                        ApiKeyCheckStatus.CHECKING -> {
                            Text(
                                text = "Validating Api Key.",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }

                        ApiKeyCheckStatus.INVALID -> {
                            Text(
                                text = "Invalid Api Key.",
                                color = bgColor,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }

                        ApiKeyCheckStatus.VALID -> {
                            Text(
                                text = "Api Key validated.",
                                color = bgColor,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }

                        ApiKeyCheckStatus.NONE -> {}
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 6.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                enabled = state.apiKeyState.isNotLoading,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                onClick = { onAction(OnboardingUIAction.GetApiKey) },
            ) {
                Icon(
                    modifier = Modifier.padding(end = 6.dp),
                    imageVector = Icons.Rounded.Key,
                    contentDescription = "Get Key",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
                Text(text = "Get Key", fontWeight = FontWeight.Bold)
            }
            Button(
                enabled = state.apiKeyState.isNotLoading,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                onClick = { onAction(OnboardingUIAction.SaveApiKey) },
            ) {
                Icon(
                    modifier = Modifier.padding(end = 6.dp),
                    imageVector = Icons.Rounded.Save,
                    contentDescription = "Save",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
                Text(text = "Save", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContentPreview() {
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(OnboardingUIState(apiKey = "", apiKeyState = ApiKeyCheckStatus.NONE)) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            modifier = Modifier.align(Alignment.TopCenter),
            text = "GemAI",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Content(state = state) {
            when (it) {
                OnboardingUIAction.GetApiKey -> {}

                OnboardingUIAction.SaveApiKey -> {
                    scope.launch {
                        state = state.copy(apiKeyState = ApiKeyCheckStatus.CHECKING)
                        delay(2500)
                        state = state.copy(apiKeyState = ApiKeyCheckStatus.VALID)
                    }
                }

                is OnboardingUIAction.SetApiKey -> {
                    state = state.copy(apiKey = it.apiKey)
                }
            }
        }
    }
}
