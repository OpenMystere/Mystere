package io.github.mystere.onebot.v11.cqcode

import io.github.mystere.serialization.cqcode.ICQCodeMessageItemDecoder
import io.github.mystere.serialization.cqcode.ICQCodeMessageItemEncoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass


class CQCodeV11MessageItemEncoder(
    type: String,
    serializersModule: SerializersModule,
): ICQCodeMessageItemEncoder<CQCodeV11MessageItem>(
    type, serializersModule
) {
    override fun encodeCQCodeMessageItem(item: CQCodeV11MessageItem) {
        CQCodeV11MessageItem.serializer().serialize(this, item)
    }
}

class CQCodeV11MessageItemDecoder(
    origin: String, serializersModule: SerializersModule,
): ICQCodeMessageItemDecoder(
    origin, serializersModule
) {
    override fun getTypeClassDefiner(): KClass<out CQCodeV11MessageItem> {
        return CQCodeV11MessageItem.Type.valueOf(type).`class`
    }
}