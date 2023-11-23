package io.github.mystere.onebot.v12.cqcode

import io.github.mystere.serialization.cqcode.ICQCodeMessageDecoder
import io.github.mystere.serialization.cqcode.ICQCodeMessageEncoder
import io.github.mystere.serialization.cqcode.ICQCodeMessageItemDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.modules.SerializersModule

class CQCodeV12MessageDecoder(
    origin: String, serializersModule: SerializersModule,
    serializer: KSerializer<CQCodeV12Message>,
    itemSerializer: KSerializer<CQCodeV12MessageItem>,
): ICQCodeMessageDecoder<CQCodeV12Message, CQCodeV12MessageItem>(
    origin, serializersModule, serializer, itemSerializer
) {
    override fun itemDescriptor(): SerialDescriptor {
        return serialDescriptor<CQCodeV12MessageItem>()
    }

    override fun newRawTextItem(text: String): CQCodeV12MessageItem {
        return CQCodeV12MessageItem.Text(text)
    }

    override fun newMessage(items: ArrayDeque<CQCodeV12MessageItem>): CQCodeV12Message {
        return CQCodeV12Message(items)
    }

    override fun newItemDecoder(
        origin: String, serializersModule: SerializersModule
    ): ICQCodeMessageItemDecoder {
        return CQCodeV12MessageItemDecoder(origin, serializersModule)
    }
}

class CQCodeV12MessageEncoder(
    serializersModule: SerializersModule,
): ICQCodeMessageEncoder<CQCodeV12Message, CQCodeV12MessageItem>(
    serializersModule
) {
    override fun encodeStringItem(item: CQCodeV12MessageItem): String {
        return (item as CQCodeV12MessageItem.Text).text
    }

    override fun newItemEncoder(
        item: CQCodeV12MessageItem,
        serializersModule: SerializersModule
    ): CQCodeV12MessageItemEncoder {
        return CQCodeV12MessageItemEncoder(item._type, serializersModule)
    }
}