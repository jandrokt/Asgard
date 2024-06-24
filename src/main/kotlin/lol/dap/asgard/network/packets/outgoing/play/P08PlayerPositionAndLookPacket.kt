package lol.dap.asgard.network.packets.outgoing.play

import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.annotations.SerialOrder
import lol.dap.asgard.utilities.Vec3D

@Packet(id = 0x08)
data class P08PlayerPositionAndLookPacket(
    @SerialOrder(1) val x: Double,
    @SerialOrder(2) val y: Double,
    @SerialOrder(3) val z: Double,
    @SerialOrder(4) val yaw: Float,
    @SerialOrder(5) val pitch: Float,
    @SerialOrder(6) val flags: Byte
) : OutgoingPacket {

    constructor(x: Double, y: Double, z: Double, yaw: Float, pitch: Float)
            : this(x, y, z, yaw, pitch, 0x10)

    constructor(vec: Vec3D, yaw: Float, pitch: Float, flags: Byte)
            : this(vec.x, vec.y, vec.z, yaw, pitch, flags)

    constructor(vec: Vec3D, yaw: Float, pitch: Float)
            : this(vec.x, vec.y, vec.z, yaw, pitch, 0x10)

}