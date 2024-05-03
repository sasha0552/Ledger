package com.github.quiltservertools.ledger.commands.subcommands

import com.github.quiltservertools.ledger.Ledger
import com.github.quiltservertools.ledger.commands.BuildableCommand
import com.github.quiltservertools.ledger.commands.CommandConsts
import com.github.quiltservertools.ledger.database.ActionQueueService
import com.github.quiltservertools.ledger.database.DatabaseManager
import com.github.quiltservertools.ledger.utility.Context
import com.github.quiltservertools.ledger.utility.LiteralNode
import com.github.quiltservertools.ledger.utility.TextColorPallet
import com.github.quiltservertools.ledger.utility.literal
import com.github.quiltservertools.ledger.utility.translate
import kotlinx.coroutines.launch
import org.sasha0552.ledger.LedgerExpectPlatform
import net.minecraft.server.command.CommandManager
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text

object StatusCommand : BuildableCommand {
    override fun build(): LiteralNode =
        CommandManager.literal("status")
            .requires(LedgerExpectPlatform.Permissions_require("ledger.commands.status", CommandConsts.PERMISSION_LEVEL))
            .executes { status(it) }
            .build()

    private fun status(context: Context): Int {
        Ledger.launch {
            val source = context.source
            source.sendFeedback(
                {
                    Text.translatable("text.ledger.header.status")
                        .setStyle(TextColorPallet.primary)
                },
                false
            )
            source.sendFeedback(
                {
                    Text.translatable(
                        "text.ledger.status.queue",
                        ActionQueueService.size.toString().literal()
                            .setStyle(TextColorPallet.secondaryVariant)
                    ).setStyle(TextColorPallet.secondary)
                },
                false
            )
            source.sendFeedback(
                {
                    Text.translatable(
                        "text.ledger.status.version",
                        LedgerExpectPlatform.getModVersionFriendlyString().literal()
                            .setStyle(TextColorPallet.secondaryVariant)
                    ).setStyle(TextColorPallet.secondary)
                },
                false
            )
            source.sendFeedback(
                {
                    Text.translatable(
                        "text.ledger.status.db_type",
                        DatabaseManager.databaseType.literal()
                            .setStyle(TextColorPallet.secondaryVariant)
                    ).setStyle(TextColorPallet.secondary)
                },
                false
            )
            source.sendFeedback(
                {
                    Text.translatable(
                        "text.ledger.status.discord",
                        "text.ledger.status.discord.join".translate()
                            .setStyle(TextColorPallet.secondaryVariant)
                            .styled {
                                it.withClickEvent(
                                    ClickEvent(
                                        ClickEvent.Action.OPEN_URL,
                                        "https://discord.gg/FpRNYrQaGP"
                                    )
                                )
                            }
                    ).setStyle(TextColorPallet.secondary)
                }, false
            )
            source.sendFeedback(
                {
                    Text.translatable(
                        "text.ledger.status.wiki",
                        "text.ledger.status.wiki.view".translate()
                            .setStyle(TextColorPallet.secondaryVariant)
                            .styled {
                                it.withClickEvent(
                                    ClickEvent(
                                        ClickEvent.Action.OPEN_URL,
                                        "https://quiltservertools.github.io/Ledger/latest/"
                                    )
                                )
                            }
                    ).setStyle(TextColorPallet.secondary)
                }, false
            )
        }

        return 1
    }
}
