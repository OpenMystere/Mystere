package io.github.mystere.onebot.v11

import io.github.mystere.onebot.IOneBotActionResp
import io.github.mystere.core.util.MystereJson
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class OneBotV11ActionResp(
    @SerialName("status")
    val status: IOneBotActionResp.Status,
    @SerialName("retcode")
    val retcode: RetCode,
    @SerialName("message")
    val message: String = "success.",
    @SerialName("data")
    val data: JsonElement? = null,
    @SerialName("echo")
    val echo: JsonElement? = null,
): IOneBotActionResp {
    @Serializable
    sealed interface Data: IOneBotActionResp.Data
    
    @Serializable(with = RetCodeSerializer::class)
    sealed class RetCode(
        override val rawCode: Long,
    ): IOneBotActionResp.RetCode {
        data object BadRequest: RetCode(1400)
        data object Unauthorized: RetCode(1401)
        data object Forbidden: RetCode(1403)
        data object NotFound: RetCode(1404)
        data class Custom(override val rawCode: Long): RetCode(rawCode)

        init {
            if (this !is Custom) {
                codes[rawCode] = this
            }
        }
    }
}
private val codes: HashMap<Long, OneBotV11ActionResp.RetCode> = hashMapOf()
object RetCodeSerializer: KSerializer<OneBotV11ActionResp.RetCode> {
    override val descriptor: SerialDescriptor = serialDescriptor<Long>()

    override fun deserialize(decoder: Decoder): OneBotV11ActionResp.RetCode {
        return with(decoder.decodeLong()) {
            codes[this] ?: OneBotV11ActionResp.RetCode.Custom(this)
        }
    }

    override fun serialize(encoder: Encoder, value: OneBotV11ActionResp.RetCode) {
        encoder.encodeLong(value.rawCode)
    }
}

inline fun <reified T: OneBotV11ActionResp.Data> OneBotV11ActionResp(
    status: IOneBotActionResp.Status,
    retcode: OneBotV11ActionResp.RetCode,
    data: T,
    message: String = "success.",
    echo: JsonElement? = null,
) = OneBotV11ActionResp(
    status = status,
    retcode = retcode,
    data = MystereJson.encodeToJsonElement(data),
    message = message,
    echo = echo,
)