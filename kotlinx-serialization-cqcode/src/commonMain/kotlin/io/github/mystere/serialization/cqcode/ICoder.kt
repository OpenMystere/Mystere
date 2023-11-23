package io.github.mystere.serialization.cqcode

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


interface ICQCodeEncoder: Encoder {
    fun encodeUnsupported(): Nothing = throw UnsupportedOperationException()
    override fun encodeBoolean(value: Boolean) = encodeUnsupported()
    override fun encodeByte(value: Byte) = encodeUnsupported()
    override fun encodeChar(value: Char) = encodeUnsupported()
    override fun encodeDouble(value: Double) = encodeUnsupported()
    override fun encodeFloat(value: Float) = encodeUnsupported()
    override fun encodeInline(descriptor: SerialDescriptor) = this
    override fun encodeInt(value: Int) = encodeUnsupported()
    override fun encodeLong(value: Long) = encodeUnsupported()
    @ExperimentalSerializationApi
    override fun encodeNotNullMark() { }
    @ExperimentalSerializationApi
    override fun encodeNull() { }
    override fun encodeShort(value: Short) = encodeUnsupported()
    override fun encodeString(value: String)
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        encodeUnsupported()
    }

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
            encodeStringElement(descriptor, index, value.toString())
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
            encodeNullableSerializableElement(descriptor, index, serializer, value)
        }
    }
}


interface ICQCodeDecoder: Decoder {
    fun decodeUnsupported(): Nothing = throw UnsupportedOperationException()
    override fun decodeBoolean() = decodeString().toBoolean()
    override fun decodeByte() = decodeString().toByte()
    override fun decodeChar() = decodeString().toCharArray()[0]
    override fun decodeDouble() = decodeString().toDouble()
    override fun decodeFloat() = decodeString().toFloat()
    override fun decodeInline(descriptor: SerialDescriptor) = this
    override fun decodeInt() = decodeString().toInt()
    override fun decodeLong() = decodeString().toLong()
    override fun decodeShort() = decodeUnsupported()
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        val current = decodeString()
        for (index in 0 until enumDescriptor.elementsCount) {
            if (enumDescriptor.getElementName(index) == current) {
                return index
            }
        }
        return -1
    }
    @ExperimentalSerializationApi
    override fun decodeNotNullMark() = decodeUnsupported()
    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? = null

    override fun decodeString(): String

    interface Composited: CompositeDecoder {
        val decoder: Decoder

        override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int) = decoder

        override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean {
            return decodeStringElement(descriptor, index).toBoolean()
        }

        override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte {
            return decodeStringElement(descriptor, index).toByte()
        }

        override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char {
            return decodeStringElement(descriptor, index).toCharArray()[0]
        }

        override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double {
            return decodeStringElement(descriptor, index).toDouble()
        }

        override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float {
            return decodeStringElement(descriptor, index).toFloat()
        }

        override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int {
            return decodeStringElement(descriptor, index).toInt()
        }

        override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long {
            return decodeStringElement(descriptor, index).toLong()
        }

        override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short {
            return decodeStringElement(descriptor, index).toShort()
        }

        @ExperimentalSerializationApi
        override fun <T : Any> decodeNullableSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            deserializer: DeserializationStrategy<T?>,
            previousValue: T?
        ): T? {
            return decodeSerializableElement(descriptor, index, deserializer, previousValue)
        }

        override fun <T> decodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            deserializer: DeserializationStrategy<T>,
            previousValue: T?
        ): T
    }
}
