package lol.dap.asgard

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import lol.dap.asgard.event_dispatching.AsgardEventDispatcher
import lol.dap.asgard.event_dispatching.AsgardEvents
import lol.dap.asgard.event_dispatching.events.play.PlayerLoginEvent
import lol.dap.asgard.instances.AsgardInstance
import lol.dap.asgard.instances.SlimeChunkProvider
import lol.dap.asgard.network.handling.AsgardHandlerManager
import lol.dap.asgard.network.server.AsgardServer
import lol.dap.asgard.utilities.Vec3D
import java.io.DataInputStream
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path

object Asgard {

    val logger = KotlinLogging.logger {}

    val eventDispatcher = AsgardEventDispatcher()

    val handler = AsgardHandlerManager()
    val server = AsgardServer("0.0.0.0", 25565)

    suspend fun init() {
        logger.info { "Starting Asgard..." }


        server.start()
    }

}

suspend fun main() = runBlocking {
    Asgard.init()
}