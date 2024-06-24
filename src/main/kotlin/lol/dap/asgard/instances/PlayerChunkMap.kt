package lol.dap.asgard.instances

import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.network.packets.outgoing.play.P26MapChunkBulkPacket
import kotlin.math.pow
import kotlin.math.sqrt

class PlayerChunkMap(
    val chunkProvider: ChunkProvider,
    val radius: Int
) {

    companion object {

        private const val CHUNKS_PER_PACKET = 4

    }

    private val playerChunks: MutableMap<PlayerEntity, MutableSet<Chunk>> = mutableMapOf()

    fun addPlayer(player: PlayerEntity) {
        playerChunks[player] = mutableSetOf()
    }

    fun removePlayer(player: PlayerEntity) {
        playerChunks.remove(player)
    }

    suspend fun updatePlayerChunks(player: PlayerEntity) {
        val chunks = playerChunks[player] ?: return
        chunks.clear()

        val playerChunk = chunkProvider.getChunkAt(Chunk.Position.fromTridimensional(player.position)) ?: return
        val chunkRadius = sqrt(radius.toDouble().pow(2) / 2).toInt()

        for (dx in -chunkRadius..chunkRadius) {
            for (dz in -chunkRadius..chunkRadius) {
                val chunk =
                    chunkProvider.getChunkAt(playerChunk.position.x + dx, playerChunk.position.z + dz) ?: continue
                chunks.add(chunk)
            }
        }

        val chunksToSend = playerChunks[player]!!
            .filter { chunk ->
                chunk.sections
                    .flatMap { it.blocks.toList() }
                    .isNotEmpty()
            }
            .chunked(CHUNKS_PER_PACKET)

        for (chunkPair in chunksToSend) {
            player.client?.writePacket(P26MapChunkBulkPacket(true, chunkPair))
        }
    }

    fun getChunksForPlayer(player: PlayerEntity): Set<Chunk>? {
        return playerChunks[player]
    }

}