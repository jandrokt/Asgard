package lol.dap.asgard.utilities

data class Vec3D(
    val x: Double,
    val y: Double,
    val z: Double
) {

    fun equals(other: Vec3D): Boolean {
        return x == other.x && y == other.y && z == other.z
    }

}