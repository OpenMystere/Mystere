package io.github.mystere.app.util

import kotlinx.coroutines.*

fun CoroutineScope.runBlockingWithCancellation(
    block: suspend () -> Unit,
    exit: suspend (Exception?) -> Unit,
) {
    addShutdownHook {
        runBlocking {
            exit(null)
            cancel()
        }
    }
    runBlocking {
        this@runBlockingWithCancellation.launch {
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

expect fun addShutdownHook(
    block: () -> Unit,
)