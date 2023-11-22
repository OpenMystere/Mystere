package io.github.mystere.serialization.cqcode

import io.github.mystere.core.util.logger
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

class CQCodeMessageItemDecoder(
    private val origin: String,
    private val type: CQCodeMessageItem.Type<*>,
    override val serializersModule: SerializersModule,
): ICQCodeDecoder {
    private val log by logger()

    private val args: Map<String, String> by lazy {
        with(origin.substring(type.name.length + 5, origin.length - 1)) {
            HashMap<String, String>().also {
                for (item in split(",")) {
                    if (!item.contains("=")) {
                        continue
                    }
                    val keyValue = item.split("=")
                    it[keyValue[0]] = keyValue[1]
                }
            }
        }
    }

    override fun decodeString() = origin
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return Composited(this, args, serializersModule)
    }

    private class Composited(
        override val decoder: Decoder,
        private val args: Map<String, String>,
        override val serializersModule: SerializersModule,
    ): ICQCodeDecoder.Composited {
        private val log by logger()

        private var elementIndex = 0

        override fun endStructure(descriptor: SerialDescriptor) {
            elementIndex = -1
        }

        override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
            var name: String
            do {
                if (elementIndex < 0) {
                    throw IllegalStateException("Call beginStructure() first")
                }
                if (elementIndex >= descriptor.elementsCount) {
                    return CompositeDecoder.DECODE_DONE
                }
                name = descriptor.getElementName(elementIndex)
                if (args.containsKey(name)) {
                    break
                }
                elementIndex += 1
            } while (true)
            return elementIndex++
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
            return deserializer.deserialize(
                CQCodeMessageItemDecoder(
                args[name]!!, CQCodeMessageItem.Type.Text, serializersModule
            )
            )
        }
    }
}
