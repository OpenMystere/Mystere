package io.github.mystere.qqsdk.qqapi.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RichMediaRespDto(
    @SerialName("file_uuid")
    val fileUuid: String,
    @SerialName("file_info")
    val fileInfo: String,
    @SerialName("ttl")
    val ttl: Int,
)