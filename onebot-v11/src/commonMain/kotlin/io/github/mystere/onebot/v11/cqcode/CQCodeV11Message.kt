package io.github.mystere.onebot.v11.cqcode

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

@Serializable(with = CQCodeV11MessageSerializer::class)
class CQCodeV11Message(
    chain: ArrayDeque<CQCodeV11MessageItem>,
): ICQCodeMessage<CQCodeV11MessageItem>(chain)

object CQCodeV11MessageSerializer: ICQCodeMessageSerializer<CQCodeV11Message, CQCodeV11MessageItem>() {
    override val descriptor: SerialDescriptor = listSerialDescriptor<CQCodeV11MessageItem>()

    override fun serialize(encoder: Encoder, value: CQCodeV11Message) {
        if (encoder is CQCodeV11MessageEncoder) {
            encoder.encodeCQCodeMessage(value)
        } else {
            super.serialize(encoder, value)
        }
    }

    override fun deserialize(decoder: Decoder): CQCodeV11Message {
        return if (decoder is CQCodeV11MessageDecoder) {
            decoder.decodeCQCodeMessage()
        } else {
            super.deserialize(decoder)
        }
    }

    override fun newDecoder(
        string: String,
        serializersModule: SerializersModule
    ): ICQCodeMessageDecoder<CQCodeV11Message, CQCodeV11MessageItem> {
        return CQCodeV11MessageDecoder(
            string, serializersModule, this,
            CQCodeV11MessageItem.serializer(),
        )
    }

    override fun decodeFromJsonElement(json: Json, type: String, obj: JsonObject): CQCodeV11MessageItem {
        return json.decodeFromJsonElement(CQCodeV11MessageItem.serializer(), obj["data"]!!.jsonObject)
    }

    override fun getCQMessageItemTypeDefiner(type: String): KClass<out ICQCodeMessageItem> {
        return CQCodeV11MessageItem.Type.valueOf(type).`class`
    }

    override fun encodeItemToJsonElement(json: Json, item: CQCodeV11MessageItem): JsonElement {
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

    override fun CQCodeMessage(items: ArrayDeque<CQCodeV11MessageItem>): CQCodeV11Message {
        return CQCodeV11Message(items)
    }
}

fun CQCode.decodeV11FromJson(json: JsonArray): CQCodeV11Message {
    return CQCodeJson.decodeFromJsonElement(json)
}
fun CQCode.decodeV11FromString(string: String): CQCodeV11Message {
    try {
        return if (string.startsWith("[{\"") && string.endsWith("\"}]")) {
            CQCodeJson.decodeFromString(CQCodeV11Message.serializer(), string)
        } else {
            CQCodeV11Message.serializer()
                .deserialize(CQCodeV11MessageDecoder(
                    string, serializersModule,
                    CQCodeV11Message.serializer(),
                    CQCodeV11MessageItem.serializer(),
                ))
        }
    } catch (e: Exception) {
        throw SerializationException("Failed to decode content: $string", e)
    }
}
fun CQCode.encodeToJson(value: CQCodeV11Message): JsonArray {
    return CQCodeJson.encodeToJsonElement(value).jsonArray
}
fun CQCode.encodeToString(value: CQCodeV11Message): String {
    val encoder = CQCodeV11MessageEncoder(serializersModule)
    encoder.encodeCQCodeMessage(value)
    return encoder.encodeFinalResult()
}
