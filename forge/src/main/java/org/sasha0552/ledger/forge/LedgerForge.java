package org.sasha0552.ledger.forge;

import com.github.quiltservertools.ledger.Ledger;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Ledger.MOD_ID)
public class LedgerForge {
    public LedgerForge() {
        if (FMLEnvironment.dist.isClient()) {
            return;
        }

        /////

        EventBuses.registerModEventBus(Ledger.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        /////

        Ledger.onInitializeServer();
    }
}
