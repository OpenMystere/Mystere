package io.github.mystere.serialization.cqcode

import kotlin.test.Test
import kotlin.test.assertTrue

class CQCodeTest {
    @Test
    fun singleFaceTest() {
        val faceId = 142L
        val message = fromString("singleFaceTest", "[CQ:face,id=$faceId]")
        assertTrue("items not only one") { message.size == 1 }
        assertTrue("item not a Face") { message[0].type == CQCodeMessageItem.Type.Face }
        assertTrue("Face id not $faceId") { (message[0] as CQCodeMessageItem.Face).id == faceId }
    }

    private fun fromString(callFrom: String, string: String): CQCodeMessage {
        return CQCode.decodeFromString(string).also {
            println("fromString<$callFrom>: $it")
        }
    }
}