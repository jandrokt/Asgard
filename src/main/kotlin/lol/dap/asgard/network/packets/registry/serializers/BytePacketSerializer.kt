package lol.dap.asgard.network.packets.registry.serializers

import lol.dap.asgard.Asgard
import lol.dap.asgard.gson.AsgardGson
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Ignored
import lol.dap.asgard.network.packets.annotations.Optional
import lol.dap.asgard.network.packets.annotations.SerialOrder
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.VariableByteBuffer
import lol.dap.asgard.network.types.extensions.toVarInt
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
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

        typeSerializers[UUID::class] = { byteBuffer, value ->
            value as UUID
            // 128 bits
            byteBuffer.putULong(value.mostSignificantBits.toULong())
            byteBuffer.putULong(value.leastSignificantBits.toULong())
        }

        typeSerializers[Collection::class] = { byteBuffer, value ->
            value as Collection<*>
            byteBuffer.putVarInt(value.size.toVarInt())
            value.forEach {
                if (it == null) return@forEach

                serializeProperty(byteBuffer, it)
            }
        }

        typeSerializers[Component::class] = { byteBuffer, value ->
            byteBuffer.putString(GsonComponentSerializer.colorDownsamplingGson().serialize(value as Component))
        }
    }

    private fun serializeProperty(buffer: VariableByteBuffer, value: Any) {
        val serializer = findSerializer(value)

        if (serializer != null) {
            // If a specific serializer is defined for this type, use it
            serializer(buffer, value)
        } else if (value is Collection<*>) {
            // If the value is a collection, serialize each element of the collection
            for (element in value) {
                if (element != null) {
                    serializeProperty(buffer, element)
                }
            }
        } else if (value::class.isData) {
            val members = value::class.declaredMemberProperties
                .sortedBy { it.findAnnotation<SerialOrder>()?.order ?: Int.MAX_VALUE }

            for (member in members) {
                if (member.findAnnotation<Optional>() != null && member.call(value) == null)
                    continue

                if (member.findAnnotation<Ignored>() != null)
                    continue

                serializeProperty(buffer, member.call(value) ?: continue)
            }
        }
    }

    private fun findSerializer(value: Any) = typeSerializers.entries.find { it.key.isInstance(value) }?.value

    fun <T : OutgoingPacket> serialize(packet: T): VariableByteBuffer {
        val buffer = VariableByteBuffer()

        serializeProperty(buffer, packet)

        return buffer
    }

}
