package lol.dap.asgard.instances.instances

import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.instances.chunk_providers.ChunkProvider
import lol.dap.asgard.instances.data.Chunk
import lol.dap.asgard.network.packets.outgoing.play.P26MapChunkBulkPacket
import kotlin.collections.chunked
import kotlin.collections.filter
import kotlin.collections.flatMap
import kotlin.collections.isNotEmpty
import kotlin.collections.set
import kotlin.collections.toList
import kotlin.math.pow
import kotlin.math.sqrt

class AsgardPlayerChunkMap(
    val chunkProvider: ChunkProvider,
    val radius: Int
) : PlayerChunkMap {

    companion object {
        private const val CHUNKS_PER_PACKET = 4
    }

    private val playerChunks: MutableMap<PlayerEntity, MutableSet<Chunk>> = mutableMapOf()

    override fun addPlayer(player: PlayerEntity) {
        playerChunks[player] = mutableSetOf()
    }

    override fun removePlayer(player: PlayerEntity) {
        playerChunks.remove(player)
    }

    override suspend fun updatePlayerChunks(player: PlayerEntity) {
        val currentChunks = playerChunks[player] ?: return
        val newChunks = mutableSetOf<Chunk>()

        val playerChunk = chunkProvider.getChunkAt(Chunk.Position.fromTridimensional(player.position)) ?: return
        val chunkRadius = sqrt(radius.toDouble().pow(2) / 2).toInt()

        for (dx in -chunkRadius..chunkRadius) {
            for (dz in -chunkRadius..chunkRadius) {
                val chunk = chunkProvider.getChunkAt(playerChunk.position.x + dx, playerChunk.position.z + dz)
                if (chunk != null) {
                    newChunks.add(chunk)
                }
            }
        }

        val chunksToAdd = newChunks - currentChunks

        currentChunks.clear()
        currentChunks.addAll(newChunks)

        sendNewChunks(player, chunksToAdd)
    }

    private suspend fun sendNewChunks(player: PlayerEntity, chunksToAdd: Set<Chunk>) {
        val chunksToSend = chunksToAdd
            .filter { chunk ->
                chunk.sections
                    .flatMap { it.blocks.toList() }
                    .isNotEmpty()
            }
            .chunked(CHUNKS_PER_PACKET)

        for (chunkBatch in chunksToSend) {
            player.client?.writePacket(P26MapChunkBulkPacket(true, chunkBatch))
        }
    }

    override fun getActiveChunks(player: PlayerEntity): Set<Chunk>? {
        return playerChunks[player]
    }

}
