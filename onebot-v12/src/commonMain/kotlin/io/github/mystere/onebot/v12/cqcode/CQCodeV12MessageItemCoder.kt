package io.github.mystere.onebot.v12.cqcode

import io.github.mystere.serialization.cqcode.ICQCodeMessageItemDecoder
import io.github.mystere.serialization.cqcode.ICQCodeMessageItemEncoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass


class CQCodeV12MessageItemEncoder(
    type: String,
    serializersModule: SerializersModule,
): ICQCodeMessageItemEncoder<CQCodeV12MessageItem>(
    type, serializersModule
) {
    override fun encodeCQCodeMessageItem(item: CQCodeV12MessageItem) {
        CQCodeV12MessageItem.serializer().serialize(this, item)
    }
}

class CQCodeV12MessageItemDecoder(
    origin: String, serializersModule: SerializersModule,
): ICQCodeMessageItemDecoder(
    origin, serializersModule
) {
    override fun getTypeClassDefiner(): KClass<out CQCodeV12MessageItem> {
        return CQCodeV12MessageItem.Type.valueOf(type).`class`
    }
}