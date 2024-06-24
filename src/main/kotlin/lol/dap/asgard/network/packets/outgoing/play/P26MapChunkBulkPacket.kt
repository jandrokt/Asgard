package lol.dap.asgard.network.packets.outgoing.play

import lol.dap.asgard.instances.data.Chunk
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.annotations.SerialOrder
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.VariableByteBuffer
import lol.dap.asgard.network.types.extensions.toVarInt

@Packet(id = 0x26)
data class P26MapChunkBulkPacket(
    @SerialOrder(1) val skylightSent: Boolean,
    @SerialOrder(2) val chunkColumnCount: VarInt,
    @SerialOrder(3) val data: ByteArray
) : OutgoingPacket {

    constructor(skylightSent: Boolean, chunks: List<Chunk>) : this(
        skylightSent,
        chunks.size.toVarInt(),
        createDataBuffer(chunks)
    )

    companion object {

        private fun createDataBuffer(chunks: List<Chunk>): ByteArray {
            val buffer = VariableByteBuffer()
            val chunkData = chunks.map { it.toBytes(groundUpContinuous = true) }

            for ((i, chunk) in chunks.withIndex()) {
                buffer.putInt(chunk.position.x)
                buffer.putInt(chunk.position.z)
                buffer.putUShort(chunkData[i].second)
            }

            for (i in chunks.indices) {
                val dataBytes = chunkData[i].first
                //buffer.putVarInt(dataBytes.size.toVarInt())
                buffer.putBytes(dataBytes)
            }

            return buffer.toByteArray()
        }

    }
}
