package org.sasha0552.ledger.fabric;

import com.github.quiltservertools.ledger.Ledger;
import net.fabricmc.api.DedicatedServerModInitializer;

public class LedgerFabric implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Ledger.onInitializeServer();
    }
}
