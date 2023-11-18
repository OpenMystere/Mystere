package io.github.mystere.serialization.cqcode

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

class CQCodeMessageEncoder(
    override val serializersModule: SerializersModule,
): ICQCodeEncoder {
    fun encodeCQCodeMessage(value: CQCodeMessage) {
        with(beginStructure(listSerialDescriptor<CQCodeMessageItem>())) {
            for (index in 0 until value.size) {
                when (val item = value[index]) {
                    is CQCodeMessageItem.Text -> encodeStringElement(serialDescriptor<String>(), index, item.text)
                    else -> {
                        val encoder = CQCodeMessageItemEncoder(serializersModule)
                        item._type.serialize(encoder, item)
                        encodeStringElement(serialDescriptor<String>(), index, encoder.encodeFinalResult())
                    }
                }
            }
        }
    }


    private val items = HashMap<Int, String>()
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        items.clear()
        return Composited(this, items, serializersModule)
    }

    override fun encodeString(value: String) {
        items.clear()
        items[-1] = value
    }

    fun encodeFinalResult(): String {
        if (items.size == 1 && items.containsKey(-1)) {
            return items[-1]!!
        }
        return StringBuilder().also {
            for (index in 0 until items.size) {
                it.append(items[index]!!)
            }
        }.toString()
    }

    private class Composited(
        private val encoder: Encoder,
        private val items: HashMap<Int, String>,
        override val serializersModule: SerializersModule,
    ): ICQCodeEncoder.Composited {
        private var elementIndex = 0
        override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int) = encoder
        override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
            items[index] = value
        }

        override fun endStructure(descriptor: SerialDescriptor) {
            elementIndex = -1
        }

        override fun <T : Any> encodeNullableSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T?
        ) {
            value?.let { encodeStringElement(descriptor, index, it.toString()) }
        }
    }
}