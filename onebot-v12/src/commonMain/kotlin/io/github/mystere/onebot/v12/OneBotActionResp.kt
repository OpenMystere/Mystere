package io.github.mystere.onebot.v12

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
data class OneBotV12ActionResp(
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
        override val rawCode: Int,
    ): IOneBotActionResp.RetCode {
        // 0 成功（OK）
        data object Success: RetCode(0)

        // 1xxxx 动作请求错误（Request Error）
        data object BadRequest: RetCode(10001)
        data object UnsupportedAction: RetCode(10002)
        data object BadParam: RetCode(10003)
        data object UnsupportedParam: RetCode(10004)
        data object UnsupportedSegment: RetCode(10005)
        data object BadSegmentData: RetCode(10006)
        data object UnsupportedSegmentData: RetCode(10007)
        data object WhoAmI: RetCode(10101)
        data object UnknownSelf: RetCode(10102)

        // 2xxxx 动作处理器错误（Handler Error）
        data object BadHandler: RetCode(20001)
        data object InternalHandlerError: RetCode(20002)

        // 3xxxx 动作执行错误（Execution Error）
        sealed class ExecutionError(override val rawCode: Int): RetCode(rawCode)
        // 31xxx
        data class DatabaseError(override val rawCode: Int): ExecutionError(rawCode)
        // 32xxx
        data class FilesystemError(override val rawCode: Int): ExecutionError(rawCode)
        // 33xxx
        data class NetworkError(override val rawCode: Int): ExecutionError(rawCode)
        // 34xxx
        data class PlatformError(override val rawCode: Int): ExecutionError(rawCode)
        // 35xxx
        data class LogicError(override val rawCode: Int): ExecutionError(rawCode)
        // 36xxx
        data class IAmTired(override val rawCode: Int): ExecutionError(rawCode)

        data class Custom(override val rawCode: Int): RetCode(rawCode)
    }
}
private val codes: HashMap<Int, OneBotV12ActionResp.RetCode> = hashMapOf(
    0 to OneBotV12ActionResp.RetCode.Success,

    10001 to OneBotV12ActionResp.RetCode.BadRequest,
    10002 to OneBotV12ActionResp.RetCode.UnsupportedAction,
    10003 to OneBotV12ActionResp.RetCode.BadParam,
    10004 to OneBotV12ActionResp.RetCode.UnsupportedParam,
    10005 to OneBotV12ActionResp.RetCode.UnsupportedSegment,
    10006 to OneBotV12ActionResp.RetCode.BadSegmentData,
    10007 to OneBotV12ActionResp.RetCode.UnsupportedSegmentData,
    10101 to OneBotV12ActionResp.RetCode.WhoAmI,
    10102 to OneBotV12ActionResp.RetCode.UnknownSelf,

    20001 to OneBotV12ActionResp.RetCode.BadHandler,
    20002 to OneBotV12ActionResp.RetCode.InternalHandlerError,
)
object RetCodeSerializer: KSerializer<OneBotV12ActionResp.RetCode> {
    override val descriptor: SerialDescriptor = serialDescriptor<Long>()

    override fun deserialize(decoder: Decoder): OneBotV12ActionResp.RetCode {
        with(decoder.decodeInt()) {
            codes[this]?.let { return it }
            val checkH2: Int = this / 1000
            return when (checkH2) {
                31 -> OneBotV12ActionResp.RetCode.DatabaseError(this)
                32 -> OneBotV12ActionResp.RetCode.FilesystemError(this)
                33 -> OneBotV12ActionResp.RetCode.NetworkError(this)
                34 -> OneBotV12ActionResp.RetCode.PlatformError(this)
                35 -> OneBotV12ActionResp.RetCode.LogicError(this)
                36 -> OneBotV12ActionResp.RetCode.IAmTired(this)
                else -> OneBotV12ActionResp.RetCode.Custom(this)
            }
        }
    }

    override fun serialize(encoder: Encoder, value: OneBotV12ActionResp.RetCode) {
        encoder.encodeInt(value.rawCode)
    }
}

inline fun <reified T: OneBotV12ActionResp.Data> OneBotV12ActionResp(
    status: IOneBotActionResp.Status,
    retcode: OneBotV12ActionResp.RetCode,
    data: T,
    message: String = "success.",
    echo: JsonElement? = null,
) = OneBotV12ActionResp(
    status = status,
    retcode = retcode,
    data = MystereJson.encodeToJsonElement(data),
    message = message,
    echo = echo,
)