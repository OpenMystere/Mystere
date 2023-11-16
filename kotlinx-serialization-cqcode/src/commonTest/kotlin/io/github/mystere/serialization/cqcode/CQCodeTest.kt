package io.github.mystere.serialization.cqcode

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class CQCodeTest {
    @Test
    fun singleFaceTest() {
        fromString("[CQ:face,id=142]") {
            assertSize(1)
            assertEquals(0, CQCodeMessageItem.Face(
                id = 142L
            ))
        }
    }
}