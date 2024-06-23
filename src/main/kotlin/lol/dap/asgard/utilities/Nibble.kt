package lol.dap.asgard.utilities

data class Nibble(
    val value: Byte
) {

    companion object {

        // two nibbles
        fun fromByte(byte: Byte): Pair<Nibble, Nibble> {
            return Pair(Nibble((byte.toInt() and 0xF0).toByte()), Nibble((byte.toInt() and 0x0F).toByte()))
        }

    }

    fun combine(other: Nibble): Byte {
        return (value.toInt() and 0xF0 or other.value.toInt() and 0x0F).toByte()
    }

}