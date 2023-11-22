package io.github.mystere.core.util

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.logging.*

fun HttpClientConfig<*>.withLogging(
    log: KLogger,
    debug: Boolean = false,
) {
    install(Logging) {
        level = if (debug) LogLevel.ALL else LogLevel.HEADERS
        logger = object : Logger {
            override fun log(message: String) {
                log.debug { message }
            }
        }
    }
}

fun Any.logger() = lazy {
    KotlinLogging.logger(this::class.qualifiedName!!)
}