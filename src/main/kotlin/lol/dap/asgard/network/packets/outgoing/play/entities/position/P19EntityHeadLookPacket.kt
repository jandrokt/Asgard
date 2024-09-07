package lol.dap.asgard.network.packets.outgoing.play.entities.position

import lol.dap.asgard.entities.Entity
import lol.dap.asgard.extensions.toByteDegrees
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.extensions.toVarInt

@Packet(id = 0x19)
data class P19EntityHeadLookPacket(
    val entityId: VarInt,
    val headYaw: Byte
) : OutgoingPacket {

    constructor(entity: Entity) : this(
        entity.id.toVarInt(),
        entity.yaw.toByteDegrees()
    )

}
