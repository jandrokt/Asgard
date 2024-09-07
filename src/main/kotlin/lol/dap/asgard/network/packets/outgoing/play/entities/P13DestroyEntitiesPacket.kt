package lol.dap.asgard.network.packets.outgoing.play.entities

import lol.dap.asgard.entities.Entity
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.extensions.toVarInt

@Packet(id = 0x13)
data class P13DestroyEntitiesPacket(
    val entityIds: List<VarInt>
) : OutgoingPacket {

    constructor(vararg entities: Entity) : this(entities.map { it.id.toVarInt() })

}