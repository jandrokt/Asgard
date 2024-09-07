package lol.dap.asgard.utilities

import kotlin.math.floor
import kotlin.math.sqrt

data class Vec3D(
    val x: Double,
    val y: Double,
    val z: Double
) {

    companion object {

        const val FRACTION_BITS = 5

        val ZERO = Vec3D(0.0, 0.0, 0.0)

        fun fromFixedPoint(x: Int, y: Int, z: Int): Vec3D {
            return Vec3D(
                x.toDouble() / (1 shl FRACTION_BITS),
                y.toDouble() / (1 shl FRACTION_BITS),
                z.toDouble() / (1 shl FRACTION_BITS)
            )
        }

    }

    fun distanceTo(other: Vec3D): Double {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z

        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    operator fun minus(other: Vec3D): Vec3D {
        return Vec3D(x - other.x, y - other.y, z - other.z)
    }

    fun toFixedPoint(): Triple<Int, Int, Int> {
        return Triple(
            floor(x * (1 shl FRACTION_BITS)).toInt(),
            floor(y * (1 shl FRACTION_BITS)).toInt(),
            floor(z * (1 shl FRACTION_BITS)).toInt()
        )
    }

}