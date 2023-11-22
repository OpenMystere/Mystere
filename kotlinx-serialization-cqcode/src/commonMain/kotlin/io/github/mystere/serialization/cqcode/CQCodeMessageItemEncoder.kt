package io.github.mystere.serialization.cqcode

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.SerializersModule

class CQCodeMessageItemEncoder(
    private val type: CQCodeMessageItem.Type<*>,
    override val serializersModule: SerializersModule,
): ICQCodeEncoder {
    private val args = hashMapOf<String, String>()
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        args.clear()
        return Composited(this, args, serializersModule)
    }

    override fun encodeString(value: String) {
        args.clear()
        args[""] = value
    }

    fun encodeFinalResult(): String {
        if (args.size == 1 && args.containsKey("")) {
            return args[""]!!
        }
        return StringBuilder().also {
            it.append("[CQ:${type.name.lowercase()}")
            for ((key, value) in args) {
                it.append(",$key=$value")
            }
            it.append("]")
        }.toString()
    }

    private class Composited(
        private val encoder: Encoder,
        private val args: HashMap<String, String>,
        override val serializersModule: SerializersModule,
    ): ICQCodeEncoder.Composited {
        private var elementIndex = 0
        override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int) = encoder
        override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
            args[descriptor.getElementName(index)] = value
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
