package io.github.mystere.serialization.cqcode

import io.github.mystere.core.util.MystereJson
import io.github.mystere.core.util.MystereJsonClassDiscriminator
import io.github.mystere.core.util.logger
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass

abstract class ICQCodeMessage<ItemT: ICQCodeMessageItem> protected constructor(
    private val chain: ArrayDeque<ItemT> = ArrayDeque()
): List<ItemT> by chain {
    override fun toString(): String {
        return StringBuilder().also {
            for (item in chain) {
                it.append(item.toString())
            }
        }.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is List<*>) {
            return false
        }
        for (index in 0 until size) {
            if (this[index] != other[index]) {
                return false
            }
        }
        return true
    }
}

interface ICQCodeMessageOperator<ItemT: ICQCodeMessageItem, MsgT: ICQCodeMessage<ItemT>> {
    operator fun plus(item: ItemT): MsgT {
        return ArrayDeque<ItemT>().also {
            it.addAll(asItemT())
            it.addLast(item)
        }.asMessage()
    }

    operator fun plus(chain: MsgT): MsgT {
        return ArrayDeque<ItemT>().also {
            it.addAll(asItemT())
            it.addAll(chain)
        }.asMessage()
    }

    fun ArrayDeque<ItemT>.asMessage(): MsgT
    fun asMessage(): MsgT {
        return ArrayDeque<ItemT>().also { it.addAll(asItemT()) }.asMessage()
    }

    fun asItemT(): MsgT {
        try {
            @Suppress("UNCHECKED_CAST")
            return this as MsgT
        } catch (e: ClassCastException) {
            throw IllegalStateException("You should always use ICQCodeMessageOperator with ICQCodeMessage!")
        }
    }
}

operator fun <ItemT: ICQCodeMessageItem, MsgT: ICQCodeMessage<ItemT>> ICQCodeMessageOperator<ItemT, MsgT>?.plus(item: ItemT): MsgT {
    return this?.plus(item) ?: (item as ICQCodeMessageItemOperator<ItemT, MsgT>).asMessage()
}

operator fun <ItemT: ICQCodeMessageItem, MsgT: ICQCodeMessage<ItemT>> ICQCodeMessageOperator<ItemT, MsgT>?.plus(items: MsgT): MsgT {
    return this?.plus(items) ?: (items as ICQCodeMessageOperator<ItemT, MsgT>).asMessage()
}

abstract class ICQCodeMessageSerializer<T: ICQCodeMessage<ItemT>, ItemT: ICQCodeMessageItem>: KSerializer<T> {
    abstract override val descriptor: SerialDescriptor

    override fun deserialize(decoder: Decoder): T {
        try {
            when (decoder) {
                is JsonDecoder -> when (val element = decoder.decodeJsonElement()) {
                    is JsonPrimitive -> {
                        if (element.isString) {
                            return deserialize(newDecoder(element.content, decoder.serializersModule))
                        }
                    }
                    is JsonArray -> {
                        val result = ArrayDeque<ItemT>()
                        for (item in element) {
                            item as JsonObject
                            val type = item["type"]?.jsonPrimitive!!.content
                            val obj = item["data"]!!.jsonObject
                            result.add(decodeFromJsonElement(
                                MystereJson, type,
                                buildJsonObject {
                                    put("type", JsonPrimitive(type))
                                    putJsonObject("data") {
                                        getCQMessageItemTypeDefiner(type)?.let {
                                            put(MystereJsonClassDiscriminator, JsonPrimitive(it.qualifiedName))
                                        }
                                        for ((key, value) in obj) {
                                            put(key, value)
                                        }
                                    }
                                },
                            ))
                        }
                        return CQCodeMessage(result)
                    }
                    else -> { }
                }
            }
            throw SerializationException("Unsupported decoder type: ${decoder::class}")
        } catch (e: Exception) {
            throw SerializationException("Failed to decoder content", e)
        }
    }

    abstract fun newDecoder(string: String, serializersModule: SerializersModule): ICQCodeMessageDecoder<T, ItemT>
    abstract fun CQCodeMessage(items: ArrayDeque<ItemT>): T
    protected abstract fun decodeFromJsonElement(json: Json, type: String, obj: JsonObject): ItemT
    protected open fun getCQMessageItemTypeDefiner(type: String): KClass<out ICQCodeMessageItem>? {
        return null
    }

    override fun serialize(encoder: Encoder, value: T) {
        try {
            when (encoder) {
                is JsonEncoder -> {
                    encoder.encodeJsonElement(buildJsonArray {
                        for (item in value) {
                            add(buildJsonObject {
                                for ((key, item) in encodeItemToJsonElement(MystereJson, item).jsonObject) {
                                    if (key == MystereJsonClassDiscriminator || key.startsWith("_type")) {
                                        continue
                                    }
                                    put(key, item)
                                }
                            })
                        }
                    })
                }
                else -> throw SerializationException("Unsupported encoder type: ${encoder::class}")
            }
        } catch (e: Exception) {
            throw SerializationException("Failed to encoder content: $value", e)
        }
    }

    abstract fun encodeItemToJsonElement(json: Json, item: ItemT): JsonElement
}

abstract class ICQCodeMessageDecoder<T: ICQCodeMessage<ItemT>, ItemT: ICQCodeMessageItem>(
    private val origin: String,
    override val serializersModule: SerializersModule,
    protected val serializer: KSerializer<T>,
    private val itemSerializer: KSerializer<ItemT>,
): ICQCodeDecoder {
    @OptIn(InternalSerializationApi::class)
    fun decodeCQCodeMessage(): T {
        with(beginStructure(serializer.descriptor)) {
            val result = HashMap<Int, ItemT>()
            var index: Int
            do {
                index = decodeElementIndex(serializer.descriptor)
                if (index == CompositeDecoder.DECODE_DONE) {
                    break
                }
                val string = decodeStringElement(serializer.descriptor, index)
                try {
                    if (string.contains("\\[(.*?)]".toRegex())) {
                        if (!string.startsWith("[CQ:") || !string.endsWith("]")) {
                            throw SerializationException("Not a valid CQCode item: $string")
                        }
                        val decoder = newItemDecoder(string, serializersModule)
                        val classDiscriminator = decoder.getTypeClassDefiner()?.qualifiedName
                        if (itemSerializer is AbstractPolymorphicSerializer<ItemT> && classDiscriminator != null) {
                            result[index] = itemSerializer.findPolymorphicSerializer(
                                decoder.beginStructure(itemDescriptor()),
                                classDiscriminator
                            ).deserialize(decoder)
                        } else {
                            result[index] = itemSerializer.deserialize(decoder)
                        }
                    } else {
                        result[index] = newRawTextItem(string)
                    }
                } catch (e: Exception) {
                    throw SerializationException("Failed to decode content: $string", e)
                }
            } while (index != CompositeDecoder.DECODE_DONE)
            return newMessage(ArrayDeque<ItemT>(result.size).also {
                for (index in 0 until result.size) {
                    it.add(result[index]!!)
                }
            })
        }
    }

    open fun itemDescriptor(): SerialDescriptor {
        throw NotImplementedError("ICQCodeMessageDecoder#itemDescriptor")
    }
    abstract fun newRawTextItem(text: String): ItemT
    abstract fun newMessage(items: ArrayDeque<ItemT>): T
    abstract fun newItemDecoder(origin: String, serializersModule: SerializersModule): ICQCodeMessageItemDecoder

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

abstract class ICQCodeMessageEncoder<T: ICQCodeMessage<ItemT>, ItemT: ICQCodeMessageItem>(
    override val serializersModule: SerializersModule,
): ICQCodeEncoder {
    fun encodeCQCodeMessage(value: T) {
        with(beginStructure(listSerialDescriptor<ICQCodeMessageItem>())) {
            for (index in 0 until value.size) {
                val item = value[index]
                when(item._type) {
                    "text" -> {
                        encodeStringElement(serialDescriptor<String>(), index, encodeStringItem(item))
                    }
                    else -> {
                        val encoder = newItemEncoder(item, serializersModule)
                        encoder.encodeCQCodeMessageItem(item)
                        encodeStringElement(serialDescriptor<String>(), index, encoder.encodeFinalResult())
                    }
                }
            }
        }
    }

    protected abstract fun encodeStringItem(item: ItemT): String
    protected abstract fun newItemEncoder(item: ItemT, serializersModule: SerializersModule): ICQCodeMessageItemEncoder<ItemT>

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
