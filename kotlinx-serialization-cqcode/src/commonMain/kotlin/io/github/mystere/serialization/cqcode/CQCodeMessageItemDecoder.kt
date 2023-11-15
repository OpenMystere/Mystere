package io.github.mystere.serialization.cqcode

import io.github.mystere.util.logger
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.SerializersModule

class CQCodeMessageItemDecoder(
    private val origin: String,
    private val args: Map<String, String>,
    override val serializersModule: SerializersModule,
): ICQCodeMessageItemDecoder {

    override fun decodeString() = origin
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return Composited(this, args, serializersModule)
    }

    private class Composited(
        private val decoder: Decoder,
        private val args: Map<String, String>,
        override val serializersModule: SerializersModule,
    ): ICQCodeMessageItemDecoder.Composited {
        private val log by logger()

        private var elementIndex = 0

        override fun endStructure(descriptor: SerialDescriptor) {
            elementIndex = -1
        }

        override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int) = decoder

        override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
            var name: String
            do {
                if (elementIndex < 0) {
                    throw IllegalStateException("Call beginStructure() first")
                }
                elementIndex += 1
                name = descriptor.getElementName(elementIndex)
            } while (args.containsKey(name))
            return elementIndex
        }

        override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String {
            return args[descriptor.getElementName(index)]!!
        }

        override fun <T> decodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            deserializer: DeserializationStrategy<T>,
            previousValue: T?
        ): T {
            val name = descriptor.getElementName(index)
            log.warn { "decodeSerializableElement: $name" }
            return deserializer.deserialize(CQCodeMessageItemDecoder(
                args[name]!!, mapOf(), serializersModule
            ))
        }
    }
}

interface ICQCodeMessageItemDecoder: Decoder {
    fun decodeUnsupported(): Nothing = throw UnsupportedOperationException()
    override fun decodeBoolean() = decodeUnsupported()
    override fun decodeByte() = decodeUnsupported()
    override fun decodeChar() = decodeUnsupported()
    override fun decodeDouble() = decodeUnsupported()
    override fun decodeEnum(enumDescriptor: SerialDescriptor) = decodeUnsupported()
    override fun decodeFloat() = decodeUnsupported()
    override fun decodeInline(descriptor: SerialDescriptor) = this
    override fun decodeInt() = decodeUnsupported()
    override fun decodeLong() = decodeUnsupported()
    @ExperimentalSerializationApi
    override fun decodeNotNullMark() = decodeUnsupported()
    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? = null
    override fun decodeShort() = decodeUnsupported()
    override fun decodeString(): String

    interface Composited: CompositeDecoder {
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
