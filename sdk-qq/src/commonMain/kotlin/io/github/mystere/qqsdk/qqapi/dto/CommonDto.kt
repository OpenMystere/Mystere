package io.github.mystere.qqsdk.qqapi.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = CodeMessageDataDto.Serializer::class)
data class CodeMessageDataDto(
    @SerialName("code")
    val code: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("data")
    val data: JsonElement = JsonNull,
): Exception(message) {
    object Serializer: KSerializer<CodeMessageDataDto> {
        override val descriptor: SerialDescriptor = serialDescriptor<CodeMessageDataDto>()

        override fun deserialize(decoder: Decoder): CodeMessageDataDto {
            decoder as JsonDecoder
            val element = decoder.decodeJsonElement().jsonObject
            return CodeMessageDataDto(
                code = element["code"]!!.jsonPrimitive.int,
                message = element["message"]!!.jsonPrimitive.content,
                data = element["data"] ?: JsonNull,
            )
        }

        override fun serialize(encoder: Encoder, value: CodeMessageDataDto) {
            encoder as JsonEncoder
            encoder.encodeJsonElement(buildJsonObject {
                put("code", JsonPrimitive(value.code))
                put("message", JsonPrimitive(value.message))
                value.data.takeIf { it !is JsonNull }?.let {
                    put("data", it)
                }
            })
        }
    }
}