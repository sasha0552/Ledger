package com.github.quiltservertools.ledger

import com.github.quiltservertools.ledger.config.config as realConfig
import com.github.quiltservertools.ledger.actionutils.ActionSearchParams
import com.github.quiltservertools.ledger.actionutils.Preview
import com.github.quiltservertools.ledger.api.ExtensionManager
import com.github.quiltservertools.ledger.api.LedgerApi
import com.github.quiltservertools.ledger.api.LedgerApiImpl
import com.github.quiltservertools.ledger.commands.registerCommands
import com.github.quiltservertools.ledger.config.CONFIG_PATH
import com.github.quiltservertools.ledger.config.DatabaseSpec
import com.github.quiltservertools.ledger.database.ActionQueueService
import com.github.quiltservertools.ledger.database.DatabaseManager
import com.github.quiltservertools.ledger.listeners.registerBlockListeners
import com.github.quiltservertools.ledger.listeners.registerEntityListeners
import com.github.quiltservertools.ledger.listeners.registerPlayerListeners
import com.github.quiltservertools.ledger.listeners.registerWorldEventListeners
import com.github.quiltservertools.ledger.network.Networking
import com.github.quiltservertools.ledger.registry.ActionRegistry
import com.uchuhimo.konf.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.LifecycleEvent
import org.sasha0552.ledger.LedgerExpectPlatform
import net.minecraft.registry.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.exposed.sql.vendors.SQLiteDialect
import java.nio.file.Files
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import com.github.quiltservertools.ledger.config.config as realConfig

object Ledger : CoroutineScope {
    const val MOD_ID = "ledger"
    val DEFAULT_DATABASE = SQLiteDialect.dialectName

    @JvmStatic
    val api: LedgerApi = LedgerApiImpl

    val logger: Logger = LogManager.getLogger("Ledger")
    lateinit var config: Config
    lateinit var server: MinecraftServer
    val searchCache = ConcurrentHashMap<String, ActionSearchParams>()
    @JvmField // Required for mixin access
    val previewCache = ConcurrentHashMap<UUID, Preview>()

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    fun onInitializeServer() {
        val version = LedgerExpectPlatform.getModVersionFriendlyString()
        logInfo("Initializing Ledger $version")

        if (!Files.exists(LedgerExpectPlatform.getConfigDir().resolve(CONFIG_PATH))) {
            logInfo("No config file, Creating")
            Files.copy(
                LedgerExpectPlatform.getModFile(CONFIG_PATH),
                LedgerExpectPlatform.getConfigDir().resolve(CONFIG_PATH)
            )
        }
        realConfig.validateRequired()
        config = realConfig

        LifecycleEvent.SERVER_STARTING.register(::serverStarting)
        LifecycleEvent.SERVER_STOPPED.register(::serverStopped)
        CommandRegistrationEvent.EVENT.register { dispatcher, _, _ -> registerCommands(dispatcher) }
    }

    private fun serverStarting(server: MinecraftServer) {
        this.server = server
        ExtensionManager.serverStarting(server)
        DatabaseManager.setup(ExtensionManager.getDataSource())
        DatabaseManager.ensureTables()

        ActionRegistry.registerDefaultTypes()
        initListeners()
        Networking

        Ledger.launch {
            val idSet = setOf<Identifier>()
                .plus(Registries.BLOCK.ids)
                .plus(Registries.ITEM.ids)
                .plus(Registries.ENTITY_TYPE.ids)

            logInfo("Inserting ${idSet.size} registry keys into the database...")
            DatabaseManager.insertIdentifiers(idSet)
            logInfo("Registry insert complete")

            DatabaseManager.setupCache()
            DatabaseManager.autoPurge()
        }.invokeOnCompletion {
            ActionQueueService.start()
        }
    }

    private fun serverStopped(server: MinecraftServer) {
        runBlocking {
            try {
                withTimeout(config[DatabaseSpec.queueTimeoutMin].minutes) {
                    Ledger.launch(Dispatchers.Default) {
                        while (ActionQueueService.size > 0) {
                            logInfo(
                                "Database is still busy. If you exit now data WILL be lost. Actions in queue: ${ActionQueueService.size}"
                            )

                            delay(config[DatabaseSpec.queueCheckDelaySec].seconds)
                        }
                    }
                    ActionQueueService.drainAll()
                    logInfo("Successfully drained database queue")
                }
            } catch (e: TimeoutCancellationException) {
                logWarn("Database drain timed out. ${ActionQueueService.size} actions still in queue. Data may be lost.")
            }
        }
    }

    private fun initListeners() {
        registerWorldEventListeners()
        registerPlayerListeners()
        registerBlockListeners()
        registerEntityListeners()
    }

    fun identifier(path: String) = Identifier(MOD_ID, path)
}

fun logDebug(message: String) = Ledger.logger.debug(message)
fun logInfo(message: String) = Ledger.logger.info(message)
fun logWarn(message: String) = Ledger.logger.warn(message)
fun logWarn(message: String, throwable: Throwable) = Ledger.logger.warn(message, throwable)
fun logFatal(message: String) = Ledger.logger.warn(message)
