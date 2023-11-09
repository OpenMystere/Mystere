package io.github.mystere.app.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import kotlinx.coroutines.*
import platform.posix.SIGINT
import platform.posix.signal

@OptIn(ExperimentalForeignApi::class)
actual fun addShutdownHook(
    block: () -> Unit,
) {
    signal(SIGINT, staticCFunction<Int, Unit> {
        block()
    })
}