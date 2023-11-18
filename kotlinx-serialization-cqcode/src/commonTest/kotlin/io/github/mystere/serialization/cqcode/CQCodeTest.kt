package io.github.mystere.serialization.cqcode

import kotlin.test.Test

class CQCodeTest {
    @Test
    fun faceTest() = Test(
        "faceTest",
        CQCodeMessageItem.Face(
            id = 142L
        )
    )

    @Test
    fun imageTest() = Test(
        "imageTest",
        CQCodeMessageItem.Image(
            file = "http://baidu.com/1.jpg"
        ) + CQCodeMessageItem.Image(
            file = "http://baidu.com/2.jpg",
            type = CQCodeMessageItem.Image.Type.flash,
        )
    )
}