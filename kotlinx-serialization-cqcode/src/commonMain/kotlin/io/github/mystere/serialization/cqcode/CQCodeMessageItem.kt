package io.github.mystere.serialization.cqcode

import io.github.mystere.core.util.logger
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass

interface ICQCodeMessageItem {
    val _type: String
    interface Type {
        val `class`: KClass<out ICQCodeMessageItem>
        val name: String
    }
}

interface ICQCodeMessageItemOperator<ItemT: ICQCodeMessageItem, MsgT: ICQCodeMessage<ItemT>> {
    operator fun plus(item: ItemT): MsgT {
        return ArrayDeque<ItemT>().also {
            it.addLast(asItemT())
            it.addLast(item)
        }.asMessage()
    }

    operator fun plus(chain: MsgT): MsgT {
        return ArrayDeque<ItemT>().also {
            it.addLast(asItemT())
            it.addAll(chain)
        }.asMessage()
    }

    fun ArrayDeque<ItemT>.asMessage(): MsgT
    fun asMessage(): MsgT {
        return ArrayDeque<ItemT>().also { it.add(asItemT()) }.asMessage()
    }

    fun asItemT(): ItemT {
        try {
            @Suppress("UNCHECKED_CAST")
            return this as ItemT
        } catch (e: ClassCastException) {
            throw IllegalStateException("You should always use ICQCodeMessageItemOperator with ICQCodeMessageItem!")
        }
    }
}

abstract class ICQCodeMessageItemDecoder(
    private val origin: String,
    override val serializersModule: SerializersModule,
): ICQCodeDecoder {
    protected val log by logger()

    val type: String by lazy {
        origin.substring(
            4, if (origin.contains(",")) {
                origin.indexOf(",")
            } else {
                origin.length - 4
            })
            .takeIf { it != "text" }
            ?.lowercase()
            ?: throw SerializationException("Not a valid CQCode: $origin")
    }

    val args: Map<String, String> by lazy {
        with(origin.substring(type.length + 5, origin.length - 1)) {
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

    open fun getTypeClassDefiner(): KClass<out ICQCodeMessageItem>? {
        return null
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
                object : ICQCodeMessageItemDecoder(
                    args[name]!!, serializersModule
                ) { }
            )
        }
    }
}

abstract class ICQCodeMessageItemEncoder<ItemT: ICQCodeMessageItem>(
    private val type: String,
    override val serializersModule: SerializersModule,
): ICQCodeEncoder {
    protected val log by logger()

    protected open val args: HashMap<String, String> = HashMap()

    abstract fun encodeCQCodeMessageItem(item: ItemT)

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        args.clear()
        return Composited(this, serializersModule, args)
    }

    override fun encodeString(value: String) {
        args.clear()
        args[""] = value
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        args[enumDescriptor.serialName] = enumDescriptor.getElementName(index)
    }

    fun encodeFinalResult(): String {
        if (args.size == 1 && args.containsKey("text") || type == "text") {
            return args[""] as String
        }
        return StringBuilder().also {
            it.append("[CQ:${type.lowercase()}")
            for ((key, value) in args) {
                if (key.isBlank()) {
                    continue
                }
                it.append(",$key=$value")
            }
            it.append("]")
        }.toString()
    }

    private class Composited(
        private val encoder: Encoder,
        override val serializersModule: SerializersModule,
        private val args: MutableMap<String, String>,
    ): ICQCodeEncoder.Composited {
        private val log by logger()

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
            value ?: return
            if (value is ICQCodeMessageItem) {
                serializer.serialize(encoder, value)
            } else {
                encodeStringElement(descriptor, index, value.toString())
            }
        }
    }
}
