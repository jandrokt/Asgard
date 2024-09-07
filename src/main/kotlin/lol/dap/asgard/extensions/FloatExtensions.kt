package lol.dap.asgard.extensions

fun Float.toByteDegrees(): Byte {
    return ((this * 256.0f / 360.0f).toInt()).toByte()
}
