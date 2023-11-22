package io.github.mystere.serialization.cqcode

import io.github.mystere.core.util.logger
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

class CQCodeMessageDecoder(
    private val origin: String,
    override val serializersModule: SerializersModule,
) : ICQCodeDecoder {
    fun decodeCQCodeMessage(): CQCodeMessage {
        with(beginStructure(CQCodeMessageSerializer.descriptor)) {
            val result = HashMap<Int, CQCodeMessageItem>()
            var index: Int
            do {
                index = decodeElementIndex(CQCodeMessageSerializer.descriptor)
                if (index == CompositeDecoder.DECODE_DONE) {
                    continue
                }
                val string = decodeStringElement(CQCodeMessageSerializer.descriptor, index)
                try {
                    if (string.contains("\\[(.*?)]".toRegex())) {
                        if (!string.startsWith("[CQ:") || !string.endsWith("]")) {
                            throw SerializationException("Not a valid CQCode item: $string")
                        }
                        val type = string.substring(
                            4, if (string.contains(",")) {
                                string.indexOf(",")
                            } else {
                                string.length - 4
                            })
                            .takeIf { it != "text" }
                            ?.lowercase()
                            ?: throw SerializationException("Not a valid CQCode: $string")
                        result[index] = with(CQCodeMessageItem.Type.valueOf(type)) {
                            deserialize(CQCodeMessageItemDecoder(
                                string, this, serializersModule
                            ))
                        }
                    } else {
                        result[index] = CQCodeMessageItem.Text(string)
                    }
                } catch (e: Exception) {
                    throw SerializationException("Failed to decode content: $string", e)
                }
            } while (index != CompositeDecoder.DECODE_DONE)
            return CQCodeMessage(ArrayDeque<CQCodeMessageItem>(result.size).also {
                for (index in 0 until result.size) {
                    it.add(result[index]!!)
                }
            })
        }
    }


    private val items by lazy {
        val result = ArrayDeque<String>()

        var index = 0
        val chars = origin.toCharArray()
        while (index < chars.size) {
            when (chars[index]) {
                '[' -> {
                    val startIndex = index
                    try {
                        while (chars[index] != ']') {
                            index += 1
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        throw SerializationException("Not a valid CQCode: $origin")
                    }
                    index += 1
                    result.addLast(origin.substring(startIndex, index))
                }
                else -> {
                    val startIndex = index
                    while (index < chars.size && chars[index] != '[') {
                        index += 1
                    }
                    result.addLast(origin.substring(startIndex, index))
                    index += 1
                }
            }
        }

        return@lazy result
    }

    override fun decodeString() = origin
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return Composited(this, items, serializersModule)
    }

    private class Composited(
        override val decoder: Decoder,
        private val items: ArrayDeque<String>,
        override val serializersModule: SerializersModule,
    ): ICQCodeDecoder.Composited {
        private val log by logger()

        private var elementIndex = 0

        override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
            if (elementIndex < 0) {
                throw IllegalStateException("Call beginStructure() first")
            }
            if (elementIndex >= items.size) {
                return CompositeDecoder.DECODE_DONE
            }
            return elementIndex++
        }

        override fun endStructure(descriptor: SerialDescriptor) {
            elementIndex = -1
        }

        override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String {
            return items[index]
        }

        override fun <T> decodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            deserializer: DeserializationStrategy<T>,
            previousValue: T?
        ): T = throw UnsupportedOperationException()
    }
}