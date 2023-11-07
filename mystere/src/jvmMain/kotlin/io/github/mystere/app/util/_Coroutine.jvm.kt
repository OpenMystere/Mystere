package io.github.mystere.app.util

import kotlinx.coroutines.*

actual fun runBlockingWithCancellation(
    block: suspend () -> Unit,
    exit: suspend (Exception?) -> Unit,
) {
    val scope = CoroutineScope(Dispatchers.Default)
    Runtime.getRuntime().addShutdownHook(Thread {
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