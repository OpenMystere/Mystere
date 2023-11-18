package io.github.mystere.onebot.v11

import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.onebot.IOneBotEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
internal data class IOneBotV11Action<Param: IOneBotV11Action.Param>(
    @SerialName("params")
    val params: Param,
    @SerialName("action")
    val action: String = params.action,
    @SerialName("echo")
    val echo: String? = null,
): IOneBotAction {
    interface Param: IOneBotAction.Param {
        @Transient
        override val action: String
    }
}

data object SendPrivateMsg: IOneBotV11Action.Param {
    override val action: String = "send_private_msg"
}
