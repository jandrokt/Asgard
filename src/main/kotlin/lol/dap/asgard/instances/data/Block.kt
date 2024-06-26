package lol.dap.asgard.instances.data

import lol.dap.asgard.utilities.Vec3D

data class Block(
    var position: Vec3D,

    var material: Material,
    var data: Byte,

    val blockLight: Byte,
    val skylight: Byte
) {

    fun copy(): Block {
        return Block(position.copy(), material, data, blockLight, skylight)
    }

}