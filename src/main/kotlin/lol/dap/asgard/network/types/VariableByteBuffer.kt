package lol.dap.asgard.network.types

import lol.dap.asgard.network.types.extensions.toVarInt
import lol.dap.asgard.network.types.extensions.writeVarInt
import java.nio.ByteBuffer

class VariableByteBuffer() : Collection<Byte> {

    private val buffer = mutableListOf<Byte>()

    constructor(byteBuffer: ByteBuffer) : this() {
        byteBuffer.rewind()
        repeat(byteBuffer.limit()) {
            buffer.add(byteBuffer.get())
        }
    }

    constructor(bytes: ByteArray) : this() {
        buffer.addAll(bytes.asList())
    }

    fun put(byte: Byte) {
        buffer.add(byte)
    }

    fun put(byte: UByte) {
        buffer.add(byte.toByte())
    }

    fun put(index: Int, byte: Byte) {
        buffer.add(index, byte)
    }

    fun putBytes(bytes: ByteArray) {
        buffer.addAll(bytes.asList())
    }

    fun putBytes(index: Int, bytes: ByteArray) {
        buffer.addAll(index, bytes.asList())
    }

    fun putVarInt(varInt: VarInt) {
        writeVarInt(varInt) {
            put(it)
        }
    }

    fun putVarInt(index: Int, varInt: VarInt) {
        var currentIndex = index

        writeVarInt(varInt) {
            put(currentIndex, it)
            currentIndex++
        }
    }

    fun putLong(long: Long) {
        repeat(8) { shift ->
            buffer.add((long shr (56 - 8 * shift)).toByte())
        }
    }

    fun putLong(index: Int, long: Long) {
        repeat(8) { shift ->
            buffer.add(index + shift, (long shr (56 - 8 * shift)).toByte())
        }
    }

    fun putInt(int: Int) {
        repeat(4) { shift ->
            buffer.add((int shr (24 - 8 * shift)).toByte())
        }
    }

    fun putInt(index: Int, int: Int) {
        repeat(4) { shift ->
            buffer.add(index + shift, (int shr (24 - 8 * shift)).toByte())
        }
    }

    fun putShort(short: Short) {
        buffer.add((short.toInt() shr 8).toByte())
        buffer.add(short.toByte())
    }

    fun putShort(index: Int, short: Short) {
        buffer.add(index, (short.toInt() shr 8).toByte())
        buffer.add(index + 1, short.toByte())
    }

    fun putString(string: String) {
        val bytes = string.toByteArray(Charsets.UTF_8)
        putVarInt(bytes.size.toVarInt())
        putBytes(bytes)
    }

    fun putString(index: Int, string: String) {
        val bytes = string.toByteArray(Charsets.UTF_8)
        putBytes(index, bytes)
        putVarInt(index, bytes.size.toVarInt())
    }

    fun putBoolean(boolean: Boolean) {
        put(if (boolean) 1 else 0)
    }

    fun putBoolean(index: Int, boolean: Boolean) {
        put(index, if (boolean) 1 else 0)
    }

    fun toByteArray(): ByteArray {
        return buffer.toByteArray()
    }

    fun toByteBuffer(): ByteBuffer {
        return ByteBuffer.wrap(toByteArray())
    }

    override val size: Int
        get() = buffer.size

    override fun contains(element: Byte): Boolean {
        return buffer.contains(element)
    }

    override fun containsAll(elements: Collection<Byte>): Boolean {
        return buffer.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return buffer.isEmpty()
    }

    override fun iterator(): Iterator<Byte> {
        return buffer.iterator()
    }

}