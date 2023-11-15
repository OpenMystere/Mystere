package io.github.mystere.util

import io.github.oshai.kotlinlogging.KotlinLogging

fun Any.logger() = lazy {
    KotlinLogging.logger(this::class.qualifiedName!!)
}