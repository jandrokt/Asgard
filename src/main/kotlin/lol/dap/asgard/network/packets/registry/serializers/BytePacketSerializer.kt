package lol.dap.asgard.network.packets.registry.serializers

import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.SerialOrder
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.VariableByteBuffer
import lol.dap.asgard.network.types.extensions.toVarInt
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
            byteBuffer.putBytes(value as ByteArray)
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

        typeSerializers[UShort::class] = { byteBuffer, value ->
            byteBuffer.putUShort(value as UShort)
        }

        typeSerializers[Float::class] = { byteBuffer, value ->
            byteBuffer.putFloat(value as Float)
        }

        typeSerializers[Double::class] = { byteBuffer, value ->
            byteBuffer.putDouble(value as Double)
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
