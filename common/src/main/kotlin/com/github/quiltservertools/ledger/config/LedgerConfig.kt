package com.github.quiltservertools.ledger.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml
import org.sasha0552.ledger.LedgerExpectPlatform

const val CONFIG_PATH = "ledger.toml"

val config: Config = Config {
    addSpec(DatabaseSpec)
    addSpec(SearchSpec)
    addSpec(ActionsSpec)
    addSpec(ColorSpec)
    addSpec(NetworkingSpec)
}
    .from.toml.resource(CONFIG_PATH)
    .from.toml.watchFile(LedgerExpectPlatform.getConfigDir().resolve("ledger.toml").toFile())
