package lol.dap.asgard.network.packets.outgoing.play.entities.position

import lol.dap.asgard.entities.Entity
import lol.dap.asgard.extensions.toByteDegrees
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.annotations.SerialOrder
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.extensions.toVarInt

@Packet(id = 0x16)
data class P16EntityLookPacket(
    @SerialOrder(1) val entityId: VarInt,
    @SerialOrder(2) val yaw: Byte,
    @SerialOrder(3) val pitch: Byte,
    @SerialOrder(4) val onGround: Boolean
) : OutgoingPacket {

    constructor(entity: Entity) : this(
        entity.id.toVarInt(),
        entity.yaw.toByteDegrees(),
        entity.pitch.toByteDegrees(),
        entity.isOnGround
    )

}
