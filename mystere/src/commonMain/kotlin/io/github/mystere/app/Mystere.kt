package io.github.mystere.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import io.github.mystere.app.util.logger
import io.github.mystere.core.MystereBot
import io.github.mystere.qq.IQQBot
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.yamlkt.Yaml
import net.mamoe.yamlkt.YamlMap

object Mystere: CliktCommand() {
    private val log by logger()

    private val configPath: String by option("-c", "--config")
        .default("./config.yaml")
        .help("Path of config file.")
        .check(
            lazyMessage = {
                "Config file $it dose not exist or not valid."
            },
            validator = {
                SystemFileSystem.metadataOrNull(Path(it))?.isRegularFile == true
            },
        )

    val Debug: Boolean by option("-d", "--debug")
        .flag()
        .help("Enable debug mode.")

    val Config: MystereConfig by lazy {
        Yaml.decodeFromString(
            MystereConfig.serializer(),
            SystemFileSystem.source(Path(configPath)).buffered().readString(),
        )
    }

    private val bots = hashSetOf<MystereBot>()

    override fun run() {
        if (Config.bots.isEmpty()) {
            log.warn { "No bot added!" }
            return
        }
        for (config in Config.bots) {
            val type = config.getStringOrNull("type")
                ?.lowercase() ?: continue
            bots.add(when (type) {
                "qq" -> IQQBot.create(Yaml.decodeFromString(
                    IQQBot.Config.serializer(),
                    config.toString(),
                ))
                else -> continue
            })
        }
    }
}

fun main(args: Array<String>) {
    Mystere.main(args)
}

@Serializable
data class MystereConfig(
    @SerialName("bots")
    val bots: List<YamlMap> = listOf()
)
