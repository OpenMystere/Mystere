package io.github.mystere.onebot.v11

import io.github.mystere.onebot.IOneBotAction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import io.github.mystere.core.util.JsonGlobal
import io.github.mystere.onebot.v11.cqcode.CQCodeV11Message
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class OneBotV11Action(
    @SerialName("params")
    val params: JsonElement,
    @SerialName("action")
    val action: Action,
    @SerialName("echo")
    val echo: JsonElement? = null,
): IOneBotAction {
    @Serializable
    sealed interface Param: IOneBotAction.Param {
        @Transient
        override val action: Action
    }
    enum class Action: IOneBotAction.Action {
        send_private_msg,
        send_guild_channel_msg,
    }



    @Serializable
    data object SendPrivateMsg: Param {
        override val action: Action = Action.send_private_msg
    }

    @Serializable
    data class SendGuildChannelMsg(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("message")
        val message: CQCodeV11Message,
    ): Param {
        override val action: Action = Action.send_guild_channel_msg
    }
}

inline fun <reified T: OneBotV11Action.Param> OneBotV11Action.withParams(crossinline block: T.() -> Unit) {
    block.invoke(JsonGlobal.decodeFromJsonElement(params))
}

inline fun <reified T: OneBotV11Action.Param> OneBotV12Action(
    params: T, echo: JsonElement? = null
): OneBotV11Action {
    return OneBotV11Action(
        params = JsonGlobal.encodeToJsonElement(params),
        action = params.action,
        echo = echo,
    )
}