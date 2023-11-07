package io.github.mystere.app.util

expect fun runBlockingWithCancellation(
    block: suspend () -> Unit,
    exit: suspend (Exception?) -> Unit,
)