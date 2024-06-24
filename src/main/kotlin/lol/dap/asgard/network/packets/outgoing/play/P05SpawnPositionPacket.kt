package lol.dap.asgard.network.packets.outgoing.play

import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.utilities.Vec3D

@Packet(id = 0x05)
data class P05SpawnPositionPacket(
    val encodedPosition: Long
) : OutgoingPacket {

    constructor(x: Long, y: Long, z: Long) : this(
        ((x and 0x3FFFFFF) shl 38) or ((y and 0xFFF) shl 26) or (z and 0x3FFFFFF))

    constructor(x: Int, y: Int, z: Int) : this(
        ((x.toLong() and 0x3FFFFFF) shl 38) or ((y.toLong() and 0xFFF) shl 26) or (z.toLong() and 0x3FFFFFF))

    constructor(vec: Vec3D) : this(
        ((vec.x.toLong() and 0x3FFFFFF) shl 38) or ((vec.y.toLong() and 0xFFF) shl 26) or (vec.z.toLong() and 0x3FFFFFF))

}