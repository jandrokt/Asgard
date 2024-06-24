package lol.dap.asgard.instances

import lol.dap.asgard.utilities.Vec3D

data class Block(
    var position: Vec3D,

    var material: UByte,
    var data: Byte,

    val blockLight: Byte,
    val skylight: Byte
)