package lol.dap.asgard.network.packets.outgoing.play.entities

import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.extensions.toByteDegrees
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.annotations.SerialOrder
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.extensions.toVarInt
import java.util.UUID

@Packet(id = 0x0C)
data class P0CSpawnPlayerPacket(
    @SerialOrder(1) val entityId: VarInt,
    @SerialOrder(2) val uuid: UUID,
    @SerialOrder(3) var x: Int, // fixed point
    @SerialOrder(4) var y: Int, // fixed point
    @SerialOrder(5) var z: Int, // fixed point
    @SerialOrder(6) val yaw: Byte, // angle
    @SerialOrder(7) val pitch: Byte, // angle
    @SerialOrder(8) val currentItem: Short,
    @SerialOrder(9) val metadata: ByteArray
) : OutgoingPacket {

    constructor(player: PlayerEntity) : this(
        player.id.toVarInt(),
        player.client!!.uuid,
        0,
        0,
        0,
        player.yaw.toByteDegrees(),
        player.pitch.toByteDegrees(),
        0,
        player.metadata.toByteArray()
    ) {
        val fixedPointPosition = player.position.toFixedPoint()
        this.x = fixedPointPosition.first
        this.y = fixedPointPosition.second
        this.z = fixedPointPosition.third
    }

}
