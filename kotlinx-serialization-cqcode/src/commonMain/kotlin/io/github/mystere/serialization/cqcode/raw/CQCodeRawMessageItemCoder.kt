package io.github.mystere.serialization.cqcode.raw

import io.github.mystere.serialization.cqcode.ICQCodeMessageItemEncoder
import io.github.mystere.serialization.cqcode.ICQCodeMessageItemDecoder
import kotlinx.serialization.modules.SerializersModule


class CQCodeRawMessageItemEncoder(
    type: String,
    serializersModule: SerializersModule,
): ICQCodeMessageItemEncoder<CQCodeRawMessageItem>(
    type, serializersModule
) {
    override fun encodeCQCodeMessageItem(item: CQCodeRawMessageItem) {
        args.clear()
        args.putAll(item.data)
    }
}

class CQCodeRawMessageItemDecoder(
    origin: String, serializersModule: SerializersModule,
): ICQCodeMessageItemDecoder(
    origin, serializersModule
)
