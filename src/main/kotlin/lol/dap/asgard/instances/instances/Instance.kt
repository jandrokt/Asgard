package lol.dap.asgard.instances.instances

import lol.dap.asgard.entities.Entity
import lol.dap.asgard.entities.EntityType
import lol.dap.asgard.instances.chunk_providers.ChunkProvider
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.utilities.Vec3D

interface Instance {

    val id: Int
    val name: String

    val spawnPosition: Vec3D
    val chunkProvider: ChunkProvider
    val entities: List<Entity>

    val chunkMap: PlayerChunkMap

    fun spawnEntity(entityType: EntityType): Entity

    fun removeEntity(entity: Entity)

    suspend fun addClient(client: Client): Entity

    fun removeClient(client: Client)

}