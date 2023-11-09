package io.github.mystere.app.util

actual fun addShutdownHook(
    block: () -> Unit,
) {
    Runtime.getRuntime().addShutdownHook(Thread {
        block()
    })
}