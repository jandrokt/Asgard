package lol.dap.asgard

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import network.handling.AsgardHandlerManager
import network.handlers.HandlerManager
import network.server.AsgardServer
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

val logger = KotlinLogging.logger {}

val networkingModule = module {
    single<HandlerManager> { AsgardHandlerManager() }
    single { AsgardServer("0.0.0.0", 25565, get()) }
}

lateinit var koin: Koin

suspend fun main() = runBlocking {
    val koinApp = startKoin {
        modules(networkingModule)
    }

    logger.info { "Starting Asgard..." }

    koin = koinApp.koin
    koin.get<AsgardServer>().start()

    stopKoin()
}