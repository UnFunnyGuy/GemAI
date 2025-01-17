package com.sarath.gem.core.util

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Retention(AnnotationRetention.RUNTIME) @Qualifier annotation class ApplicationScope

@Qualifier @Retention(RUNTIME) annotation class Dispatcher(val niaDispatcher: GemAIDispatchers)

enum class GemAIDispatchers {
    Default,
    IO,
}
