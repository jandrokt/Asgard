package extensions

fun Int.toHexRepresentation(): String {
    return "0x${this.toString(16).padStart(2, '0')}"
}