package lol.dap.asgard.instances.instances

import lol.dap.asgard.Asgard
import lol.dap.asgard.entities.Entity
import lol.dap.asgard.entities.EntityType
import lol.dap.asgard.entities.LivingEntity
import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.event_dispatching.AsgardEvents
import lol.dap.asgard.event_dispatching.events.play.PlayerEntityCreationEvent
import lol.dap.asgard.instances.chunk_providers.ChunkProvider
import lol.dap.asgard.network.packets.outgoing.play.P01JoinGamePacket
import lol.dap.asgard.network.packets.outgoing.play.P05SpawnPositionPacket
import lol.dap.asgard.network.packets.outgoing.play.P08PlayerPositionAndLookPacket
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.utilities.Vec3D
import kotlin.collections.find
import kotlin.toUByte

class AsgardInstance(
    override val id: Int,
    override val name: String,

    override val spawnPosition: Vec3D,
    override val chunkProvider: ChunkProvider,
    val viewDistance: Int,

    override val entities: MutableList<Entity>
) : Instance {

    override val chunkMap = AsgardPlayerChunkMap(chunkProvider, viewDistance)

    var entityCount = 0

    override fun spawnEntity(entityType: EntityType): Entity {
        val entity = LivingEntity(entityCount++, entityType, "Entity $entityCount", this, spawnPosition)
        entities.add(entity)

        return entity
    }

    override fun removeEntity(entity: Entity) {
        if (entity is PlayerEntity && entity.client != null) {
            removeClient(entity.client)
            return
        }

        entities.remove(entity)
    }

    override suspend fun addClient(client: Client): Entity {
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

        chunkMap.addPlayer(event.entity)

        client.writePacket(
            P01JoinGamePacket(
                entity.id,
                1.toUByte(),
                0.toByte(),
                0.toUByte(),
                100.toUByte(),
                "default",
                false
            )
        )

        chunkMap.updatePlayerChunks(event.entity)

        client.writePacket(P05SpawnPositionPacket(spawnPosition))
        client.writePacket(P08PlayerPositionAndLookPacket(spawnPosition, 0.0f, 0.0f))

        return entity
    }

    override fun removeClient(client: Client) {
        val entity = entities.find { it is PlayerEntity && it.client == client } ?: return

        if (entity is PlayerEntity) {
            chunkMap.removePlayer(entity)
            client.entity = null
        }

        entities.remove(entity)
    }

}