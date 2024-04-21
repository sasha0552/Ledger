package org.sasha0552.ledger.forge;

import com.github.quiltservertools.ledger.Ledger;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.function.Predicate;

public class LedgerExpectPlatformImpl {
    public static boolean Permissions_check(@NotNull Entity entity, @NotNull String permission, int defaultRequiredLevel) {
        return entity.hasPermissionLevel(defaultRequiredLevel);
    }

    public static Predicate<ServerCommandSource> Permissions_require(@NotNull String permission, int defaultRequiredLevel) {
        return (source) -> source.hasPermissionLevel(defaultRequiredLevel);
    }

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR
                .get();
    }

    public static Path getModFile(String file) {
        URI jarUri;

        /////

        try {
            jarUri = LedgerExpectPlatformImpl.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        /////

        return FileSystems
                .getFileSystem(jarUri)
                .getPath(file);
    }

    public static String getModVersionFriendlyString() {
        return FMLLoader
                .getLoadingModList()
                .getMods()
                .stream()
                .filter(mod -> mod.getModId().equals(Ledger.MOD_ID))
                .findFirst()
                .get()
                .getVersion()
                .toString();
    }
}
