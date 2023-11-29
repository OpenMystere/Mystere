package io.github.mystere.onebot.v12

import io.github.mystere.onebot.IOneBotAction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import io.github.mystere.core.util.MystereJson
import io.github.mystere.onebot.v12.cqcode.CQCodeV12Message
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class OneBotV12Action(
    @SerialName("params")
    val params: JsonElement,
    @SerialName("action")
    override val action: Action,
    @SerialName("self")
    val self: Self? = null,
    @SerialName("echo")
    val echo: JsonElement? = null,
): IOneBotAction {
    @Serializable
    sealed interface Param: IOneBotAction.Param
    @Serializable
    data class Self(
        @SerialName("user_id")
        val userId: String,
        @SerialName("platform")
        val platform: String,
    )
    enum class Action: IOneBotAction.Action {
        send_private_msg,
        send_guild_channel_msg,
    }



    @Serializable
    data object SendPrivateMsg: Param

    @Serializable
    data class SendGuildChannelMsg(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("message")
        val message: CQCodeV12Message,
    ): Param
}

inline fun <reified T: OneBotV12Action.Param> OneBotV12Action.withParams(crossinline block: T.() -> Unit) {
    block.invoke(MystereJson.decodeFromJsonElement(params))
}


