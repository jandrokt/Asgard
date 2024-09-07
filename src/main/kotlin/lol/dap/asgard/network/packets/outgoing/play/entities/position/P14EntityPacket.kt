package lol.dap.asgard.network.packets.outgoing.play.entities.position

import lol.dap.asgard.entities.Entity
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.extensions.toVarInt

@Packet(id = 0x14)
data class P14EntityPacket(
    val entityId: VarInt
) : OutgoingPacket {

    constructor(entity: Entity) : this(entity.id.toVarInt())

}