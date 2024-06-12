package network.packets.serializers

import network.packets.OutgoingPacket
import network.packets.annotations.SerialOrder
import network.types.VarInt
import network.types.VariableByteBuffer
import network.types.extensions.toVarInt
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

typealias Serializer = (VariableByteBuffer, Any) -> Unit

object BytePacketSerializer {

    private val typeSerializers = mutableMapOf<KClass<*>, Serializer>()

    init {
        typeSerializers[Boolean::class] = { byteBuffer, value ->
            byteBuffer.putBoolean(value as Boolean)
        }

        typeSerializers[VarInt::class] = { byteBuffer, value ->
            byteBuffer.putVarInt(value as VarInt)
        }

        typeSerializers[Byte::class] = { byteBuffer, value ->
            byteBuffer.put(value as Byte)
        }

        typeSerializers[UByte::class] = { byteBuffer, value ->
            byteBuffer.put(value as UByte)
        }

        typeSerializers[ByteArray::class] = { byteBuffer, value ->
            if (value !is ByteArray) throw IllegalArgumentException("Value must be a ByteArray")

            byteBuffer.putVarInt(value.size.toVarInt())
            byteBuffer.putBytes(value)
        }

        typeSerializers[Long::class] = { byteBuffer, value ->
            byteBuffer.putLong(value as Long)
        }

        typeSerializers[Int::class] = { byteBuffer, value ->
            byteBuffer.putInt(value as Int)
        }

        typeSerializers[Short::class] = { byteBuffer, value ->
            byteBuffer.putShort(value as Short)
        }

        typeSerializers[String::class] = { byteBuffer, value ->
            byteBuffer.putString(value as String)
        }
    }

    fun <T : OutgoingPacket> serialize(packet: T, packetClass: KClass<out T>): VariableByteBuffer {
        val members = packetClass.declaredMemberProperties
            .sortedBy { it.findAnnotation<SerialOrder>()?.order ?: Int.MAX_VALUE }
        val buffer = VariableByteBuffer()

        for (member in members) {
            val value = member.call(packet) ?: continue
            val serializer = typeSerializers[member.returnType.classifier as KClass<*>] ?: continue

            serializer(buffer, value)
        }

        return buffer
    }

}
