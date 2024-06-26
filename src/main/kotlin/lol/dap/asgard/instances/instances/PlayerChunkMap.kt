package lol.dap.asgard.instances.instances

import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.instances.data.Chunk

interface PlayerChunkMap {

    fun addPlayer(player: PlayerEntity)

    fun removePlayer(player: PlayerEntity)

    suspend fun updatePlayerChunks(player: PlayerEntity)

    fun getActiveChunks(player: PlayerEntity): Set<Chunk>?

}