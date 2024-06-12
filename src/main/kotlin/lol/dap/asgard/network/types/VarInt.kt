package network.types

@JvmInline
value class VarInt(
    private val value: Int
) {

    operator fun plus(other: VarInt): VarInt = VarInt(value + other.value)

    operator fun minus(other: VarInt): VarInt = VarInt(value - other.value)

    operator fun times(other: VarInt): VarInt = VarInt(value * other.value)

    operator fun div(other: VarInt): VarInt = VarInt(value / other.value)

    operator fun rem(other: VarInt): VarInt = VarInt(value % other.value)

    operator fun compareTo(other: VarInt): Int = value.compareTo(other.value)

    fun toInt(): Int = value

}