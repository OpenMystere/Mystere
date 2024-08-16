package io.github.mystere.app.util

import net.mamoe.yamlkt.Yaml
import net.mamoe.yamlkt.YamlBuilder

//import com.charleskorn.kaml.Yaml
//import com.charleskorn.kaml.YamlConfiguration


//val YamlGlobal = Yaml(
//    configuration = YamlConfiguration(
//
//    ),
//)

val YamlGlobal = Yaml {
    stringSerialization = YamlBuilder.StringSerialization.SINGLE_QUOTATION
    listSerialization = YamlBuilder.ListSerialization.BLOCK_SEQUENCE
}