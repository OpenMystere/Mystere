package io.github.mystere.serialization.cqcode

import io.github.mystere.onebot.v12.cqcode.CQCodeV12MessageItem
import kotlin.test.Test

class CQCodeV12Test {
    @Test
    fun mentionTest() = Test(
        "faceTest",
        CQCodeV12MessageItem.Mention(
            userId = "test-user-id"
        )
    )

    @Test
    fun imageTest() = Test(
        "imageTest",
        CQCodeV12MessageItem.Image(
            fileId = "test-file-id-1"
        ) + CQCodeV12MessageItem.Image(
            fileId = "test-file-id-2"
        )
    )
}