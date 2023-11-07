package io.github.mystere.core.util

import io.github.oshai.kotlinlogging.KotlinLogging

fun Any.logger() = lazy {
    KotlinLogging.logger(this::class.qualifiedName!!)
}