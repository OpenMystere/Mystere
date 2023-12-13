package io.github.mystere.qqsdk.qqapi.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactionTarget(
    @SerialName("id")
    val id: String,
    @SerialName("type")
    val type: ReactionTargetType,
)

typealias ReactionTargetType = Int

class MessageReaction {

}
