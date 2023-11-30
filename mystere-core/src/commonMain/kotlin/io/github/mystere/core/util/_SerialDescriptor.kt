package io.github.mystere.core.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

abstract class ListDelegateSerializer<ListT: List<ItemT>, ItemT: @Serializable Any>: KSerializer<ListT> {
    abstract override val descriptor: SerialDescriptor

    override fun deserialize(decoder: Decoder): ListT {
        val array = (decoder as JsonDecoder).decodeJsonElement().jsonArray
        val result: ArrayList<ItemT> = MystereJson.decodeFromJsonElement(array)
        return newList(result)
    }

    abstract fun newList(result: ArrayList<ItemT>): ListT

    override fun serialize(encoder: Encoder, value: ListT) {
        (encoder as JsonEncoder).encodeJsonElement(
            MystereJson.encodeToJsonElement(value as List<ItemT>)
        )
    }
}

inline fun <reified T> simpleClassSerialDescriptor(): SerialDescriptor {
    return buildClassSerialDescriptor(T::class.qualifiedName!!)
}
