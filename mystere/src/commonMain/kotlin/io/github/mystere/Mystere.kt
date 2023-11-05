package io.github.mystere

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString

object Mystere: CliktCommand() {
    val config: String by option()
        .default("./config.yaml")
        .help("Path of config file.")
        .check(
            lazyMessage = {
                "Config file $it dose not exist or not valid."
            },
            validator = {
                SystemFileSystem.metadataOrNull(Path(it))?.isRegularFile == true
            }
        )

    override fun run() {
        SystemFileSystem.source(Path(config)).buffered().readString()
    }
}

fun main(args: Array<String>) {
    Mystere.main(args)
}