package lol.dap.asgard.network.packets.registry.serializers

import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.extensions.getVarInt
import java.nio.ByteBuffer
import kotlin.reflect.KClass

typealias Deserializer = (ByteBuffer) -> Any

object BytePacketDeserializer {

    private val typeDeserializers = mutableMapOf<KClass<*>, Deserializer>()

    init {
        typeDeserializers[Boolean::class] = { buffer ->
            buffer.get() == 1.toByte()
        }

        typeDeserializers[VarInt::class] = { buffer ->
            buffer.getVarInt()
        }

        typeDeserializers[Byte::class] = { buffer ->
            buffer.get()
        }

        typeDeserializers[UByte::class] = { buffer ->
            buffer.get().toUByte()
        }

        typeDeserializers[ByteArray::class] = { buffer ->
            val length = buffer.getVarInt().toInt()
            val bytes = ByteArray(length)
            buffer.get(bytes)
            bytes
        }

        typeDeserializers[Long::class] = { buffer ->
            buffer.getLong()
        }

        typeDeserializers[Int::class] = { buffer ->
            buffer.getInt()
        }

        typeDeserializers[Short::class] = { buffer ->
            buffer.getShort()
        }

        typeDeserializers[UShort::class] = { buffer ->
            buffer.getShort().toUShort()
        }

        typeDeserializers[Float::class] = { buffer ->
            buffer.getFloat()
        }

        typeDeserializers[Double::class] = { buffer ->
            buffer.getDouble()
        }

        typeDeserializers[String::class] = { buffer ->
            val length = buffer.getVarInt().toInt()
            val bytes = ByteArray(length)
            buffer.get(bytes)
            String(bytes)
        }
    }

    inline fun <reified T : IncomingPacket> deserialize(packet: ByteBuffer): T {
        return deserialize(T::class, packet) as T
    }

    inline fun <reified T : IncomingPacket> deserialize(clazz: KClass<*>, packet: ByteBuffer): T {
        return constructPacket(clazz, packet) as T
    }

    fun constructPacket(clazz: KClass<*>, packet: ByteBuffer): Any {
        val constructors = clazz.constructors

        val constructor = constructors.first()
        val parameters = constructor.parameters
        val arguments = mutableListOf<Any?>()

        for (parameter in parameters) {
            val type = parameter.type.classifier as KClass<*>
            val deserializer = typeDeserializers[type] ?: error("No deserializer for type $type")
            arguments.add(deserializer(packet))
        }

        return constructor.call(*arguments.toTypedArray())
    }

}