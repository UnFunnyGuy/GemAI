package com.sarath.gem.navigation.graph

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootGraph

@NavGraph<RootGraph>(route = "main_route", start = true) annotation class MainGraph

@NavGraph<MainGraph>(route = "chat_route", start = true) annotation class ChatGraph

@NavGraph<RootGraph>(route = "onboarding_route") annotation class OnboardingGraph
