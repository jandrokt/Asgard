package lol.dap.asgard.utilities

data class Vec3D(
    val x: Double,
    val y: Double,
    val z: Double
) {

    fun distanceTo(other: Vec3D): Double {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z

        return Math.sqrt(dx * dx + dy * dy + dz * dz)
    }

}