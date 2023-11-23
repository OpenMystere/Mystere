package io.github.mystere.onebot.v12.cqcode

import io.github.mystere.serialization.cqcode.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass

@Serializable(with = CQCodeV12MessageSerializer::class)
class CQCodeV12Message(
    chain: ArrayDeque<CQCodeV12MessageItem>,
): ICQCodeMessage<CQCodeV12MessageItem>(chain)

object CQCodeV12MessageSerializer: ICQCodeMessageSerializer<CQCodeV12Message, CQCodeV12MessageItem>() {
    override val descriptor: SerialDescriptor = listSerialDescriptor<CQCodeV12MessageItem>()

    override fun serialize(encoder: Encoder, value: CQCodeV12Message) {
        if (encoder is CQCodeV12MessageEncoder) {
            encoder.encodeCQCodeMessage(value)
        } else {
            super.serialize(encoder, value)
        }
    }

    override fun deserialize(decoder: Decoder): CQCodeV12Message {
        return if (decoder is CQCodeV12MessageDecoder) {
            decoder.decodeCQCodeMessage()
        } else {
            super.deserialize(decoder)
        }
    }

    override fun newDecoder(
        string: String,
        serializersModule: SerializersModule
    ): ICQCodeMessageDecoder<CQCodeV12Message, CQCodeV12MessageItem> {
        return CQCodeV12MessageDecoder(
            string, serializersModule, this,
            CQCodeV12MessageItem.serializer(),
        )
    }

    override fun decodeFromJsonElement(json: Json, type: String, obj: JsonObject): CQCodeV12MessageItem {
        return json.decodeFromJsonElement(CQCodeV12MessageItem.serializer(), obj["data"]!!.jsonObject)
    }

    override fun getCQMessageItemTypeDefiner(type: String): KClass<out ICQCodeMessageItem> {
        return CQCodeV12MessageItem.Type.valueOf(type).`class`
    }

    override fun encodeItemToJsonElement(json: Json, item: CQCodeV12MessageItem): JsonElement {
        return buildJsonObject {
            put("type", item._type)
            putJsonObject("data") {
                for ((key, value) in json.encodeToJsonElement(item).jsonObject) {
                    if (key.startsWith("_type") || key == CQCodeJsonClassDiscriminator) {
                        continue
                    }
                    put(key, value)
                }
            }
        }
    }

    override fun CQCodeMessage(items: ArrayDeque<CQCodeV12MessageItem>): CQCodeV12Message {
        return CQCodeV12Message(items)
    }
}

fun CQCode.decodeV12FromJson(json: JsonArray): CQCodeV12Message {
    return CQCodeJson.decodeFromJsonElement(json)
}
fun CQCode.decodeV12FromString(string: String): CQCodeV12Message {
    try {
        return if (string.startsWith("[{\"") && string.endsWith("\"}]")) {
            CQCodeJson.decodeFromString(CQCodeV12Message.serializer(), string)
        } else {
            CQCodeV12Message.serializer()
                .deserialize(CQCodeV12MessageDecoder(
                    string, serializersModule,
                    CQCodeV12Message.serializer(),
                    CQCodeV12MessageItem.serializer(),
                ))
        }
    } catch (e: Exception) {
        throw SerializationException("Failed to decode content: $string", e)
    }
}
fun CQCode.encodeToJson(value: CQCodeV12Message): JsonArray {
    return CQCodeJson.encodeToJsonElement(value).jsonArray
}
fun CQCode.encodeToString(value: CQCodeV12Message): String {
    val encoder = CQCodeV12MessageEncoder(serializersModule)
    encoder.encodeCQCodeMessage(value)
    return encoder.encodeFinalResult()
}
