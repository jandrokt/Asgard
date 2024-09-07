package lol.dap.asgard.instances.instances

import lol.dap.asgard.Asgard
import lol.dap.asgard.entities.Entity
import lol.dap.asgard.entities.EntityType
import lol.dap.asgard.entities.LivingEntity
import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.event_dispatching.AsgardEvents
import lol.dap.asgard.event_dispatching.events.play.PlayerEntityCreationEvent
import lol.dap.asgard.instances.chunk_providers.ChunkProvider
import lol.dap.asgard.instances.instances.player_chunk_map.AsgardPlayerChunkMap
import lol.dap.asgard.instances.instances.player_map.AsgardPlayerMap
import lol.dap.asgard.network.packets.outgoing.play.P01JoinGamePacket
import lol.dap.asgard.network.packets.outgoing.play.P05SpawnPositionPacket
import lol.dap.asgard.network.packets.outgoing.play.P08PlayerPositionAndLookPacket
import lol.dap.asgard.network.packets.outgoing.play.entities.P1CEntityMetadataPacket
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.utilities.Vec3D
import lol.dap.asgard.utilities.tick_loop.TickLoop
import kotlin.collections.find
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.toUByte

class AsgardInstance(
    override val id: Int,
    override val name: String,

    override val spawnPosition: Vec3D,
    override val chunkProvider: ChunkProvider,
    val renderDistance: Int,
    val entityViewDistance: Int,

    override val entities: MutableList<Entity>
) : Instance {

    override val playerChunkMap = AsgardPlayerChunkMap(chunkProvider, renderDistance)
    override val playerMap = AsgardPlayerMap(entityViewDistance)

    var entityCount = 0

    override val tickLoop = TickLoop("Instance $name", 20)

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

        tickLoop.scheduleJob {
            P01JoinGamePacket(
                entity.id,
                entity.gameMode.id.toUByte(),
                0.toByte(),
                0.toUByte(),
                100.toUByte(),
                "default",
                false
            ).send(client)

            P05SpawnPositionPacket(spawnPosition).send(client)
            P08PlayerPositionAndLookPacket(spawnPosition, 0.0f, 0.0f).send(client)

            playerChunkMap.addPlayer(event.entity)
            playerChunkMap.updatePlayerChunks(event.entity)

            tickLoop.scheduleJob(1.seconds) {
                playerMap.addPlayer(entity)
                playerMap.showToPlayers(entity)
            }
            //playerMap.syncTeleportPlayer(entity)
        }

        return entity
    }

    override fun removeClient(client: Client) {
        val entity = entities.find { it is PlayerEntity && it.client == client } ?: return

        if (entity is PlayerEntity) {
            playerChunkMap.removePlayer(entity)
            tickLoop.scheduleJob {
                playerMap.removePlayer(entity)
                playerMap.permanentlyHideFromPlayers(entity)
            }

            client.entity = null
        }

        entities.remove(entity)
    }

    override fun updateEntityMetadata(entity: Entity) {
        val closePlayers = entities
            .filter { it is PlayerEntity }
            .map { it as PlayerEntity }
            .filter { it.position.distanceTo(entity.position) <= entityViewDistance }
            .filter { it != entity }
        val packet = P1CEntityMetadataPacket(entity)
        entity.metadata.clearChangedMetadata()

        tickLoop.scheduleJob {
            for (player in closePlayers) {
                packet.send(player.client)
            }
        }
    }

}