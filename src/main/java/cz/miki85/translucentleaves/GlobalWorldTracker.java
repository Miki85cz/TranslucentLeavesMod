package cz.miki85.translucentleaves;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class GlobalWorldTracker {

    private static Path getGlobalFilePath() {
        return FMLPaths.CONFIGDIR.get().resolve("translucentleaves_pending_worlds.txt");
    }

    public static synchronized void markWorldAsModified(ServerLevel level) {
        try {
            Path file = getGlobalFilePath();
            String worldFolderName = level.getServer().getWorldPath(LevelResource.ROOT).getFileName().toString();

            Set<String> pendingWorlds = new HashSet<>();
            if (Files.exists(file)) {
                pendingWorlds.addAll(Files.readAllLines(file));
            }

            if (pendingWorlds.add(worldFolderName)) {
                Files.write(file, pendingWorlds, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}