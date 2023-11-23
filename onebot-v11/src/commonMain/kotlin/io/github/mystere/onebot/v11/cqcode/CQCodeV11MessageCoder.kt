package io.github.mystere.onebot.v11.cqcode

import io.github.mystere.serialization.cqcode.ICQCodeMessageDecoder
import io.github.mystere.serialization.cqcode.ICQCodeMessageEncoder
import io.github.mystere.serialization.cqcode.ICQCodeMessageItemDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.modules.SerializersModule

class CQCodeV11MessageDecoder(
    origin: String, serializersModule: SerializersModule,
    serializer: KSerializer<CQCodeV11Message>,
    itemSerializer: KSerializer<CQCodeV11MessageItem>,
): ICQCodeMessageDecoder<CQCodeV11Message, CQCodeV11MessageItem>(
    origin, serializersModule, serializer, itemSerializer
) {
    override fun itemDescriptor(): SerialDescriptor {
        return serialDescriptor<CQCodeV11MessageItem>()
    }

    override fun newRawTextItem(text: String): CQCodeV11MessageItem {
        return CQCodeV11MessageItem.Text(text)
    }

    override fun newMessage(items: ArrayDeque<CQCodeV11MessageItem>): CQCodeV11Message {
        return CQCodeV11Message(items)
    }

    override fun newItemDecoder(
        origin: String, serializersModule: SerializersModule
    ): ICQCodeMessageItemDecoder {
        return CQCodeV11MessageItemDecoder(origin, serializersModule)
    }
}

class CQCodeV11MessageEncoder(
    serializersModule: SerializersModule,
): ICQCodeMessageEncoder<CQCodeV11Message, CQCodeV11MessageItem>(
    serializersModule
) {
    override fun encodeStringItem(item: CQCodeV11MessageItem): String {
        return (item as CQCodeV11MessageItem.Text).text
    }

    override fun newItemEncoder(
        item: CQCodeV11MessageItem,
        serializersModule: SerializersModule
    ): CQCodeV11MessageItemEncoder {
        return CQCodeV11MessageItemEncoder(item._type, serializersModule)
    }
}