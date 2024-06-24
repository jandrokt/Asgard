package lol.dap.asgard.utilities

data class Vec3D(
    val x: Double,
    val y: Double,
    val z: Double
) {

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Vec3D -> x == other.x && y == other.y && z == other.z
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

}