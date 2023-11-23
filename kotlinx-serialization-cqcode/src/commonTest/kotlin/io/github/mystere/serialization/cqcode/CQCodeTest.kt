package io.github.mystere.serialization.cqcode

import io.github.mystere.serialization.cqcode.raw.CQCodeRawMessageItem
import kotlin.test.Test

class CQCodeTest {
    @Test
    fun faceTest() = Test(
        "faceTest",
        CQCodeRawMessageItem(
            _type = "face",
            data = mapOf("id" to "142"),
        )
    )

    @Test
    fun imageTest() = Test(
        "imageTest",
        CQCodeRawMessageItem(
            _type = "image",
            data = mapOf("file" to "http://baidu.com/1.jpg"),
        ) + CQCodeRawMessageItem(
            _type = "image",
            data = mapOf(
                "file" to "http://baidu.com/2.jpg",
                "type" to "flash",
            )
        )
    )
}