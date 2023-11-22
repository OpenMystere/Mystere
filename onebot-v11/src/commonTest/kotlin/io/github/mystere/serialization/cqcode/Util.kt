package io.github.mystere.serialization.cqcode

import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun Test(testName: String, item: CQCodeMessageItem) {
   Test(testName, item.asMessage())
}
fun Test(testName: String, origin: CQCodeMessage) {
    println("================== $testName ==================")
    println("origin message: $origin")
    val string = CQCode.encodeToString(origin)
    println("encode string result: $string")
    val json = CQCode.encodeToJson(origin)
    println("encode json result: $json")
    val item = CQCode.decodeFromString(string)
    println("decode result: $item")
    println("================== $testName ==================\n")
    assertEquals(origin, item, "origin item is $origin, but decode result is $item")
}
