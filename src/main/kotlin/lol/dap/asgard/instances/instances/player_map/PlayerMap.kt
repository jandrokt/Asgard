package lol.dap.asgard.instances.instances.player_map

import lol.dap.asgard.entities.PlayerEntity
import java.util.UUID

/**
 * Player Maps keep track of every player in an instance. The goal is to propagate player data to all players in the instance.
 */
interface PlayerMap : Collection<PlayerEntity> {

    val players: List<PlayerEntity>

    suspend fun addPlayer(player: PlayerEntity)

    suspend fun removePlayer(player: PlayerEntity)

    fun getPlayer(name: String): PlayerEntity?

    fun getPlayer(uuid: UUID): PlayerEntity?

    fun getPlayer(id: Int): PlayerEntity?

    fun clear()

    suspend fun showToPlayers(player: PlayerEntity)

    suspend fun permanentlyHideFromPlayers(player: PlayerEntity)

    suspend fun handlePlayer(player: PlayerEntity)

    suspend fun handlePlayerLackOfMovement(player: PlayerEntity)

    suspend fun handlePlayerMove(player: PlayerEntity)

    suspend fun handlePlayerLook(player: PlayerEntity)

    suspend fun handlePlayerMoveAndLook(player: PlayerEntity)

    suspend fun syncTeleportPlayer(player: PlayerEntity)

}