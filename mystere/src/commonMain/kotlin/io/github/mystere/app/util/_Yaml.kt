package io.github.mystere.app.util

import net.mamoe.yamlkt.Yaml
import net.mamoe.yamlkt.YamlBuilder

val YamlGlobal = Yaml {
    stringSerialization = YamlBuilder.StringSerialization.SINGLE_QUOTATION
    listSerialization = YamlBuilder.ListSerialization.BLOCK_SEQUENCE
}