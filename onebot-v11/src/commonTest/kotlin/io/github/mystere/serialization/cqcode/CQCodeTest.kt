package io.github.mystere.serialization.cqcode

import io.github.mystere.onebot.v11.cqcode.CQCodeV11MessageItem
import kotlin.test.Test

class CQCodeV11Test {
    @Test
    fun faceTest() = Test(
        "faceTest",
        CQCodeV11MessageItem.Face(
            id = 142L
        )
    )

    @Test
    fun imageTest() = Test(
        "imageTest",
        CQCodeV11MessageItem.Image(
            file = "http://baidu.com/1.jpg",
            type = CQCodeV11MessageItem.Image.Type.flash,
        ) + CQCodeV11MessageItem.Image(
            file = "http://baidu.com/2.jpg",
        )
    )
}