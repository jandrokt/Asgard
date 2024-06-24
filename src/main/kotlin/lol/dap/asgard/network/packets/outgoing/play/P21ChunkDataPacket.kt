package lol.dap.asgard.network.packets.outgoing.play

import lol.dap.asgard.instances.Chunk
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.annotations.SerialOrder
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.extensions.toVarInt

@Packet(id = 0x21)
data class P21ChunkDataPacket(
    @SerialOrder(1) val chunkX: Int,
    @SerialOrder(2) val chunkZ: Int,
    @SerialOrder(3) val groundUpContinuous: Boolean,
    @SerialOrder(4) var primaryBitMask: UShort = 0.toUShort(),
    @SerialOrder(5) var size: VarInt = 0.toVarInt(),
    @SerialOrder(6) var data: ByteArray = byteArrayOf()
) : OutgoingPacket {

    constructor(chunk: Chunk) : this(chunk.position.x, chunk.position.z, true) {
        val (data, primaryBitMask) = chunk.toBytes(groundUpContinuous = true)
        this.data = data
        this.primaryBitMask = primaryBitMask
        this.size = data.size.toVarInt()
    }

}