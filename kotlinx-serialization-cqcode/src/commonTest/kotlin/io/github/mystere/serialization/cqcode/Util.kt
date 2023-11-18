package io.github.mystere.serialization.cqcode

import kotlin.test.assertEquals
import kotlin.test.assertTrue


fun fromString(string: String, block: CQCodeMessage.() -> Unit) {
    with(CQCode.decodeFromString(string), block)
}

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

fun CQCodeMessage.assertSize(size: Int) {
    kotlin.with(this@assertSize.size) {
        assertTrue("items size is not $size, but $this") { this == size }
    }
}
fun CQCodeMessage.with(index: Int, block: CQCodeMessageItem.() -> Unit) {
    with(this[index], block)
}
inline fun <reified T: CQCodeMessageItem> CQCodeMessageItem.assertEquals(item: T) {
    kotlin.test.assertIs<T>(this, "item is not ${T::class.simpleName}, but ${this::class.simpleName}")
    kotlin.test.assertEquals(this, item, "expect item is $item, but $this")
}
inline fun <reified T: CQCodeMessageItem> CQCodeMessage.assertEquals(index: Int, item: T) {
    with(index) {
        assertEquals(item)
    }
}