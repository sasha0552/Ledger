package org.sasha0552.ledger;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Predicate;

public class LedgerExpectPlatform {
    @ExpectPlatform
    public static boolean Permissions_check(@NotNull Entity entity, @NotNull String permission, int defaultRequiredLevel) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Predicate<ServerCommandSource> Permissions_require(@NotNull String permission, int defaultRequiredLevel) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getConfigDir() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getModFile(String file) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String getModVersionFriendlyString() {
        throw new AssertionError();
    }
}
