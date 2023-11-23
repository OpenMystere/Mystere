package io.github.mystere.serialization.cqcode.raw

import io.github.mystere.serialization.cqcode.ICQCodeMessage
import io.github.mystere.serialization.cqcode.ICQCodeMessageDecoder
import io.github.mystere.serialization.cqcode.ICQCodeMessageOperator
import io.github.mystere.serialization.cqcode.ICQCodeMessageSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule


@Serializable(with = CQCodeRawMessageSerializer::class)
class CQCodeRawMessage(
    chain: ArrayDeque<CQCodeRawMessageItem>
): ICQCodeMessage<CQCodeRawMessageItem>(chain), ICQCodeMessageOperator<CQCodeRawMessageItem, CQCodeRawMessage> {
    override fun ArrayDeque<CQCodeRawMessageItem>.asMessage(): CQCodeRawMessage {
        return CQCodeRawMessage(this)
    }
}


object CQCodeRawMessageSerializer: ICQCodeMessageSerializer<CQCodeRawMessage, CQCodeRawMessageItem>() {
    override val descriptor: SerialDescriptor = listSerialDescriptor<CQCodeRawMessageItem>()

    override fun serialize(encoder: Encoder, value: CQCodeRawMessage) {
        if (encoder is CQCodeRawMessageEncoder) {
            encoder.encodeCQCodeMessage(value)
        } else {
            super.serialize(encoder, value)
        }
    }

    override fun deserialize(decoder: Decoder): CQCodeRawMessage {
        return if (decoder is CQCodeRawMessageDecoder) {
            return decoder.decodeCQCodeMessage()
        } else {
            super.deserialize(decoder)
        }
    }

    override fun newDecoder(
        string: String,
        serializersModule: SerializersModule
    ): ICQCodeMessageDecoder<CQCodeRawMessage, CQCodeRawMessageItem> {
        return CQCodeRawMessageDecoder(string, serializersModule)
    }

    override fun decodeFromJsonElement(json: Json, type: String, obj: JsonObject): CQCodeRawMessageItem {
        return json.decodeFromJsonElement(obj)
    }

    override fun encodeItemToJsonElement(json: Json, item: CQCodeRawMessageItem): JsonElement {
        return json.encodeToJsonElement(item)
    }

    override fun CQCodeMessage(items: ArrayDeque<CQCodeRawMessageItem>): CQCodeRawMessage {
        return CQCodeRawMessage(items)
    }
}
