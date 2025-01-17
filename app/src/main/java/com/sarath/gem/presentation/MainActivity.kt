package com.sarath.gem.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.sarath.gem.core.base.collectState
import com.sarath.gem.presentation.theme.GemAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var isSplashScreenOn by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This should be called before enableEdgeToEdge, else there will be weird unwanted actions
        // bar
        installSplashScreen().apply { setKeepOnScreenCondition { isSplashScreenOn } }
        enableEdgeToEdge()
        setContent {
            val state by viewModel.collectState()
            GemAITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (!state.isCheckingApiKey) {
                        Main(modifier = Modifier.padding(innerPadding), isApiKeySet = state.isApiKeySet)
                    }
                }
            }
            LaunchedEffect(state.isCheckingApiKey) { isSplashScreenOn = state.isCheckingApiKey }
        }
    }
}

@Composable
private fun Main(modifier: Modifier = Modifier, isApiKeySet: Boolean) {
    DestinationsNavHost(
        navGraph = NavGraphs.root,
        start = if (isApiKeySet) NavGraphs.mainRoute else NavGraphs.onboardingRoute,
        modifier = modifier,
    )
}
