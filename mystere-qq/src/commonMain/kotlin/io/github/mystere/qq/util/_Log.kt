package io.github.mystere.qq.util

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.logging.*

fun HttpClient.withLogging(
    tag: String = "HttpClient",
): HttpClient = config {
    withLogging(KotlinLogging.logger(tag))
}

fun HttpClient.withLogging(
    log: KLogger,
): HttpClient = config {
    install(Logging) {
//        level = if (debug) LogLevel.ALL else LogLevel.HEADERS
        logger = object : Logger {
            override fun log(message: String) {
                log.debug { message }
            }
        }
    }
}
