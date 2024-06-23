package lol.dap.asgard.instances

import lol.dap.asgard.utilities.Nibble
import lol.dap.asgard.utilities.Vec3D

data class Block(
    var position: Vec3D,

    var material: Int,
    var data: Nibble,

    val blockLight: Nibble,
    val skyLight: Nibble
)