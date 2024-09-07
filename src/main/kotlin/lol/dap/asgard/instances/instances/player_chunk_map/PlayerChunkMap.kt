package lol.dap.asgard.instances.instances.player_chunk_map

import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.instances.data.Chunk

interface PlayerChunkMap {

    fun addPlayer(player: PlayerEntity)

    fun removePlayer(player: PlayerEntity)

    fun updatePlayerChunks(player: PlayerEntity)

    fun getActiveChunks(player: PlayerEntity): Set<Chunk>?

}