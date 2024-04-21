package org.sasha0552.ledger.fabric;

import com.github.quiltservertools.ledger.Ledger;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Predicate;

public class LedgerExpectPlatformImpl {
    public static boolean Permissions_check(@NotNull Entity entity, @NotNull String permission, int defaultRequiredLevel) {
        return Permissions.check(entity, permission, defaultRequiredLevel);
    }

    public static Predicate<ServerCommandSource> Permissions_require(@NotNull String permission, int defaultRequiredLevel) {
        return Permissions.require(permission, defaultRequiredLevel);
    }

    public static Path getConfigDir() {
        return FabricLoader
                .getInstance()
                .getConfigDir();
    }

    public static Path getModFile(String file) {
        return FabricLoader
                .getInstance()
                .getModContainer(Ledger.MOD_ID)
                .get()
                .getPath(file);
    }

    public static String getModVersionFriendlyString() {
        return FabricLoader
                .getInstance()
                .getModContainer(Ledger.MOD_ID)
                .get()
                .getMetadata()
                .getVersion()
                .getFriendlyString();
    }
}
