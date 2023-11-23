package io.github.mystere.serialization.cqcode

import io.github.mystere.serialization.cqcode.raw.CQCodeRawMessage
import io.github.mystere.serialization.cqcode.raw.CQCodeRawMessageItem
import kotlin.test.assertEquals

fun Test(testName: String, item: CQCodeRawMessageItem) {
    Test(testName, item.asMessage())
}
fun Test(testName: String, origin: CQCodeRawMessage) {
    println("================== $testName ==================")
    println("origin message: $origin")
    val string = CQCode.encodeToString(origin)
    println("encode string result: $string")
    val strRes = CQCode.decodeRawFromString(string)
    println("decode string result: $strRes")
    val json = CQCode.encodeToJson(origin)
    println("encode json result: $json")
    val jsonRes = CQCode.decodeRawFromJson(json)
    println("encode json result: $jsonRes")
    println("================== $testName ==================\n")
}
