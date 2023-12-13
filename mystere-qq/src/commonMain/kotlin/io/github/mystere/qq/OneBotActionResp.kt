package io.github.mystere.qq

import io.github.mystere.core.util.MystereJson
import io.github.mystere.onebot.IOneBotActionResp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Serializable
sealed interface OneBotQQActionRespData: IOneBotActionResp.Data

@OptIn(ExperimentalContracts::class)
inline fun <reified T: @Serializable Any> JsonElement?.castQQCustom(block: T.() -> OneBotQQActionRespData?): OneBotQQActionRespData? {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(MystereJson.decodeFromJsonElement<T>(this@castQQCustom!!))
}