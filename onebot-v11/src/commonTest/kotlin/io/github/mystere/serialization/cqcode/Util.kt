package io.github.mystere.serialization.cqcode

import io.github.mystere.onebot.v11.cqcode.*
import kotlin.test.assertEquals

fun Test(testName: String, item: CQCodeV11MessageItem) {
    Test(testName, item.asMessage())
}
fun Test(testName: String, origin: CQCodeV11Message) {
    println("================== $testName ==================")
    println("origin message: $origin")
    val string = CQCode.encodeToString(origin)
    println("encode string result: $string")
    val strRes = CQCode.decodeV11FromString(string)
    println("decode string result: $strRes")
    assertEquals(origin, strRes, "decode string result not equals as origin")
    val json = CQCode.encodeToJson(origin)
    println("encode json result: $json")
    val jsonRes = CQCode.decodeV11FromJson(json)
    println("encode json result: $jsonRes")
    assertEquals(origin, strRes, "decode json result not equals as origin")
    println("================== $testName ==================\n")
}
