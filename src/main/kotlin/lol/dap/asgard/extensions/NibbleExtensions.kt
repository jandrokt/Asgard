package lol.dap.asgard.extensions

private fun getHighNibble(byte: Byte): Byte {
    return ((byte.toInt() shr 4) and 0x0F).toByte()
}

private fun getLowNibble(byte: Byte): Byte {
    return (byte.toInt() and 0x0F).toByte()
}

private fun combineNibbles(highNibble: Byte, lowNibble: Byte): Byte {
    return ((highNibble.toInt() shl 4) or (lowNibble.toInt() and 0x0F)).toByte()
}

private fun combineNibbles(nibbles: List<Byte>): List<Byte> {
    require(nibbles.size % 2 == 0) { "The list of nibbles must have an even number of elements." }

    return nibbles.chunked(2) { (highNibble, lowNibble) ->
        combineNibbles(highNibble, lowNibble)
    }
}

fun Byte.highNibble(): Byte = getHighNibble(this)

fun Byte.lowNibble(): Byte = getLowNibble(this)

fun Pair<Byte, Byte>.toByte(): Byte = combineNibbles(this.first, this.second)

fun List<Byte>.toBytes(): List<Byte> = combineNibbles(this)