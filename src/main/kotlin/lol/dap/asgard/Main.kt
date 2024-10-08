package lol.dap.asgard

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import lol.dap.asgard.event_dispatching.AsgardEventDispatcher
import lol.dap.asgard.event_dispatching.EventDispatcher
import lol.dap.asgard.network.handling.AsgardHandlerManager
import lol.dap.asgard.network.handling.HandlerManager
import lol.dap.asgard.network.packets.registry.AsgardPacketRegistry
import lol.dap.asgard.network.packets.registry.PacketRegistry
import lol.dap.asgard.network.server.AsgardServer
import lol.dap.asgard.network.server.repository.AsgardClientRepository
import lol.dap.asgard.network.server.repository.ClientRepository
import lol.dap.asgard.plugins.AsgardPluginLoader
import lol.dap.asgard.plugins.PluginLoader

object Asgard {

    private val logger = KotlinLogging.logger {}

    val eventDispatcher: EventDispatcher = AsgardEventDispatcher()

    val clientRepository: ClientRepository = AsgardClientRepository()
    val packetRegistry: PacketRegistry = AsgardPacketRegistry()
    val handler: HandlerManager = AsgardHandlerManager()
    val server = AsgardServer("0.0.0.0", 25565)

    val pluginLoader: PluginLoader = AsgardPluginLoader()

    suspend fun init() {
        logger.info { "Starting Asgard..." }

        addShutdownHook()

        pluginLoader.loadPlugins()
        pluginLoader.enablePlugins()

        server.start()
        server.stop()
    }

    suspend fun shutdown() {
        logger.info { "Shutting down Asgard..." }

        pluginLoader.disablePlugins()
    }

    private fun addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(Thread {
            runBlocking {
                shutdown()
            }
        })
    }

}

suspend fun main() = runBlocking {
    Asgard.init()
}