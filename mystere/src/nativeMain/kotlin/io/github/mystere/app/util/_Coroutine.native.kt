package io.github.mystere.app.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import kotlinx.coroutines.*
import platform.posix.SIGINT
import platform.posix.signal

@OptIn(ExperimentalForeignApi::class)
actual fun runBlockingWithCancellation(
    block: suspend () -> Unit,
    exit: suspend (Exception?) -> Unit,
) {
    val scope = CoroutineScope(Dispatchers.Default)
    signal(SIGINT, staticCFunction<Int, Unit> {
        runBlocking {
            exit(null)
            scope.cancel()
        }
    })
    runBlocking {
        scope.launch {
            try {
                block()
            } catch (e: Exception) {
                exit(e)
                return@launch
            }
            while (true) {
                delay(10_000)
            }
        }.join()
    }
}