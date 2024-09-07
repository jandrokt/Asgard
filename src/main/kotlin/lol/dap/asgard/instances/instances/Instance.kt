package lol.dap.asgard.instances.instances

import lol.dap.asgard.entities.Entity
import lol.dap.asgard.entities.EntityType
import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.instances.chunk_providers.ChunkProvider
import lol.dap.asgard.instances.instances.player_chunk_map.PlayerChunkMap
import lol.dap.asgard.instances.instances.player_map.PlayerMap
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.utilities.Vec3D
import lol.dap.asgard.utilities.tick_loop.TickLoop

interface Instance {

    val id: Int
    val name: String

    val spawnPosition: Vec3D
    val chunkProvider: ChunkProvider
    val entities: List<Entity>

    val playerChunkMap: PlayerChunkMap
    val playerMap: PlayerMap

    val tickLoop: TickLoop

    fun spawnEntity(entityType: EntityType): Entity

    fun removeEntity(entity: Entity)

    fun addClient(client: Client): Entity

    fun removeClient(client: Client)

    fun updateEntityMetadata(entity: Entity)

}