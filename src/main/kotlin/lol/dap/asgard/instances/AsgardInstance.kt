package lol.dap.asgard.instances

import lol.dap.asgard.Asgard
import lol.dap.asgard.entities.Entity
import lol.dap.asgard.entities.EntityType
import lol.dap.asgard.entities.LivingEntity
import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.event_dispatching.AsgardEvents
import lol.dap.asgard.event_dispatching.events.PlayerEntityCreationEvent
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.utilities.Vec3D

class AsgardInstance(
    override val id: Int,
    override val name: String,
    override val spawnPosition: Vec3D,
    override val chunkProvider: ChunkProvider,
    override val entities: MutableList<Entity>
) : Instance {

    var entityCount = 0

    override fun spawnEntity(entityType: EntityType): Entity {
        val entity = LivingEntity(entityCount++, entityType, "Entity $entityCount", this, spawnPosition)
        entities.add(entity)

        return entity
    }

    override fun removeEntity(entity: Entity) {
        if (entity is PlayerEntity && entity.client != null)
            entity.client.entity = null

        entities.remove(entity)
    }

    override fun addClient(client: Client): Entity {
        val entity = PlayerEntity(
            client = client,
            id = entityCount++,
            name = client.username,
            instance = this,
            position = spawnPosition
        )

        val event = PlayerEntityCreationEvent(client, entity)
        Asgard.eventDispatcher.dispatch(AsgardEvents.PLAYER_ENTITY_CREATION, event)

        entities.add(event.entity)
        client.entity = event.entity

        return entity
    }

    override fun removeClient(client: Client) {
        val entity = entities.find { it is PlayerEntity && it.client == client } ?: return

        if (entity is PlayerEntity) {
            client.entity = null
        }

        entities.remove(entity)
    }

}