package io.github.mystere.serialization.cqcode

import kotlin.test.assertTrue


fun fromString(string: String, block: CQCodeMessage.() -> Unit) {
    with(CQCode.decodeFromString(string), block)
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