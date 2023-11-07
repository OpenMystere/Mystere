package io.github.mystere.qq.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.logging.*

fun HttpClient.withLogging(
    tag: String = "HttpClient",
): HttpClient = config {
    install(Logging) {
        logger = object : Logger {
            private val log = KotlinLogging.logger(tag)
            override fun log(message: String) {
                log.debug { message }
            }
        }
    }
}
