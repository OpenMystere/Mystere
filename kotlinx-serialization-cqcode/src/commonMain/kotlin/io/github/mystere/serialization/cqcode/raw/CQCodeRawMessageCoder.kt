package io.github.mystere.serialization.cqcode.raw

import io.github.mystere.serialization.cqcode.ICQCodeMessageDecoder
import io.github.mystere.serialization.cqcode.ICQCodeMessageEncoder
import io.github.mystere.serialization.cqcode.ICQCodeMessageItemDecoder
import kotlinx.serialization.modules.SerializersModule


class CQCodeRawMessageDecoder(
    origin: String, serializersModule: SerializersModule,
) : ICQCodeMessageDecoder<CQCodeRawMessage, CQCodeRawMessageItem>(
    origin, serializersModule,
    CQCodeRawMessage.serializer(),
    CQCodeRawMessageItem.serializer(),
) {
    override fun newRawTextItem(text: String): CQCodeRawMessageItem {
        return CQCodeRawMessageItem("text", mapOf("text" to text))
    }

    override fun newMessage(items: ArrayDeque<CQCodeRawMessageItem>): CQCodeRawMessage {
        return CQCodeRawMessage(items)
    }

    override fun newItemDecoder(
        origin: String, serializersModule: SerializersModule
    ): ICQCodeMessageItemDecoder {
        return CQCodeRawMessageItemDecoder(origin, serializersModule)
    }
}

class CQCodeRawMessageEncoder(
    serializersModule: SerializersModule
): ICQCodeMessageEncoder<CQCodeRawMessage, CQCodeRawMessageItem>(
    serializersModule
) {
    override fun encodeStringItem(item: CQCodeRawMessageItem): String {
        return item.data["text"]!!.toString()
    }

    override fun newItemEncoder(
        item: CQCodeRawMessageItem,
        serializersModule: SerializersModule
    ): CQCodeRawMessageItemEncoder {
        return CQCodeRawMessageItemEncoder(item._type, serializersModule)
    }
}
