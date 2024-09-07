package lol.dap.asgard.network.packets.outgoing.play.entities.position

import lol.dap.asgard.entities.Entity
import lol.dap.asgard.extensions.toByteDegrees
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.extensions.toVarInt

@Packet(id = 0x18)
data class P18EntityTeleportPacket(
    val entityId: VarInt,
    var x: Int,
    var y: Int,
    var z: Int,
    val yaw: Byte,
    val pitch: Byte,
    val onGround: Boolean
) : OutgoingPacket {

    constructor(entity: Entity) : this(
        entity.id.toVarInt(),
        0,
        0,
        0,
        entity.yaw.toByteDegrees(),
        entity.pitch.toByteDegrees(),
        entity.isOnGround
    ) {
        val position = entity.position.toFixedPoint()
        x = position.first
        y = position.second
        z = position.third
    }

}