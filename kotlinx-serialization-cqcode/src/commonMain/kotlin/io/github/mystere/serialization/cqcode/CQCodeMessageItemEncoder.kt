package io.github.mystere.serialization.cqcode

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.SerializersModule

class CQCodeMessageItemEncoder(
    override val serializersModule: SerializersModule,
): ICQCodeMessageItemEncoder {
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
            it.append("[CQ:${args["type"]}")
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
    ): ICQCodeMessageItemEncoder.Composited {
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

interface ICQCodeMessageItemEncoder: Encoder {
    fun encodeUnsupported(): Nothing = throw UnsupportedOperationException()
    override fun encodeBoolean(value: Boolean) = encodeUnsupported()
    override fun encodeByte(value: Byte) = encodeUnsupported()
    override fun encodeChar(value: Char) = encodeUnsupported()
    override fun encodeDouble(value: Double) = encodeUnsupported()
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = encodeUnsupported()
    override fun encodeFloat(value: Float) = encodeUnsupported()
    override fun encodeInline(descriptor: SerialDescriptor) = this
    override fun encodeInt(value: Int) = encodeUnsupported()
    override fun encodeLong(value: Long) = encodeUnsupported()
    @ExperimentalSerializationApi
    override fun encodeNotNullMark() = encodeUnsupported()
    @ExperimentalSerializationApi
    override fun encodeNull() { }
    override fun encodeShort(value: Short) = encodeUnsupported()
    override fun encodeString(value: String)

    interface Composited: CompositeEncoder {
        override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
            encodeStringElement(descriptor, index, value.toString())
        }

        override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
            encodeStringElement(descriptor, index, value.toString())
        }

        override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
            encodeStringElement(descriptor, index, value.toString())
        }

        override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
            encodeStringElement(descriptor, index, value.toString())
        }

        override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
            encodeStringElement(descriptor, index, value.toString())
        }

        override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
            encodeStringElement(descriptor, index, value.toString())
        }

        override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
            encodeStringElement(descriptor, index, value.toString())
        }

        override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
            throw UnsupportedOperationException()
        }

        @ExperimentalSerializationApi
        override fun <T : Any> encodeNullableSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T?
        )

        override fun <T> encodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T
        ) {
            encodeSerializableElement(descriptor, index, serializer, value)
        }
    }
}
