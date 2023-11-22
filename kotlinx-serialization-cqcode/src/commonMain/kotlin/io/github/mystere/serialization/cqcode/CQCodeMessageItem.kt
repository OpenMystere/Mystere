package io.github.mystere.serialization.cqcode

import io.github.mystere.core.util.logger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

@Serializable
abstract class CQCodeMessageItem(
    @Transient
    private val _typeDelegate: Type<*>? = null,
    @Transient
    open val rawText: String? = null,
) {
    // 纯文本
    @Serializable
    data class Text(
        @SerialName("text")
        val text: String
    ): CQCodeMessageItem(Type.Text, text)

    val _rawType: Type<*> get() = _typeDelegate!!
    val _type get() = _rawType.name.lowercase()

    operator fun plus(item: CQCodeMessageItem): CQCodeMessage {
        return CQCodeMessage(ArrayDeque<CQCodeMessageItem>().also {
            it.addLast(this)
            it.addLast(item)
        })
    }
    operator fun plus(chain: CQCodeMessage): CQCodeMessage {
        return CQCodeMessage(ArrayDeque<CQCodeMessageItem>().also {
            it.addLast(this)
            it.addAll(chain)
        })
    }

    @Serializable(with = CQCodeMessageItemTypeSerializer::class)
    abstract class Type<T: CQCodeMessageItem>(
        internal val serializer: KSerializer<T>
    ) {
        object Text: Type<CQCodeMessageItem.Text>(CQCodeMessageItem.Text.serializer())


        val name: String get() = this::class.simpleName!!

        init {
            register(this)
        }

        internal fun decodeFromJsonElement(json: Json, element: JsonObject): CQCodeMessageItem {
            return json.decodeFromJsonElement(serializer, element)
        }
        internal fun deserialize(decoder: Decoder): CQCodeMessageItem {
            return serializer.deserialize(decoder)
        }
        @Suppress("UNCHECKED_CAST")
        internal inline fun <MsgT: CQCodeMessageItem> encodeToJsonElement(json: Json, item: MsgT): JsonObject {
            return json.encodeToJsonElement(serializer, item as T).jsonObject
        }
        @Suppress("UNCHECKED_CAST")
        internal inline fun <MsgT: CQCodeMessageItem> serialize(encoder: Encoder, value: MsgT) {
            serializer.serialize(encoder, value as T)
        }

        companion object {
            private val cache: HashMap<String, Type<*>> = hashMapOf()
            fun register(type: Type<*>) {
                cache[type.name.lowercase()] = type
            }

            fun valueOf(name: String): Type<*> {
                return with(name.lowercase()) {
                    cache[this] ?: throw IllegalArgumentException("No CQCodeMessageItem.Type named $this")
                }
            }
        }
    }
}

object CQCodeMessageItemTypeSerializer: KSerializer<CQCodeMessageItem.Type<*>> {
    override val descriptor: SerialDescriptor = serialDescriptor<String>()

    override fun deserialize(decoder: Decoder): CQCodeMessageItem.Type<*> {
        return CQCodeMessageItem.Type.valueOf(decoder.decodeString().lowercase())
    }

    override fun serialize(encoder: Encoder, value: CQCodeMessageItem.Type<*>) {
        encoder.encodeString(value.name.lowercase())
    }
}
