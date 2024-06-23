package lol.dap.asgard.instances

import lol.dap.asgard.entities.Entity
import lol.dap.asgard.entities.EntityType
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.utilities.Vec3D

interface Instance {

    val id: Int
    val name: String

    val spawnPosition: Vec3D
    val chunkProvider: ChunkProvider
    val entities: List<Entity>

    fun spawnEntity(entityType: EntityType): Entity

    fun removeEntity(entity: Entity)

    fun addClient(client: Client): Entity

    fun removeClient(client: Client)

}