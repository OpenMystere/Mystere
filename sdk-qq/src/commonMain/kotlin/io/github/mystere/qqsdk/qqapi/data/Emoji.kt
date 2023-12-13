package io.github.mystere.qqsdk.qqapi.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
    @SerialName("id")
    val id: String,
    @SerialName("type")
    val type: EmojiType,
)

typealias EmojiType = Int
