package lol.dap.asgard.network.types.extensions

import io.ktor.utils.io.*
import lol.dap.asgard.network.types.VarInt
import java.nio.ByteBuffer
import kotlin.experimental.and

inline fun writeVarInt(varInt: VarInt, writeByte: (Byte) -> Unit) {
    var value = varInt.toInt()

    while (true) {
        if ((value and 0xFFFFFF80.toInt()) == 0) {
            writeByte(value.toByte())
            return
        }

        writeByte(((value and 0x7F) or 0x80).toByte())
        value = value ushr 7
    }
}

inline fun readVarInt(readByte: () -> Byte): VarInt {
    var offset = 0
    var value = 0L
    var byte: Byte

    do {
        if (offset == 35) error("VarInt too long")

        byte = readByte()
        value = value or ((byte.toLong() and 0x7F) shl offset)

        offset += 7
    } while ((byte and 0x80.toByte()) != 0.toByte())

    return VarInt(value.toInt())
}

fun Int.toVarInt(): VarInt = VarInt(this)

suspend fun ByteReadChannel.readVarInt(): VarInt = readVarInt { readByte() }

fun ByteBuffer.getVarInt(): VarInt = readVarInt { get() }

suspend fun ByteWriteChannel.writeVarInt(varInt: VarInt) = writeVarInt(varInt) { writeByte(it) }
