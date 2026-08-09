package cz.miki85.translucentleaves;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.slf4j.Logger;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

@Mod("translucentleaves")
public class TranslucentLeaves {
    private static final Logger LOGGER = LogUtils.getLogger();

    public TranslucentLeaves() {
        boolean isClient = FMLLoader.getDist() == Dist.CLIENT;
        LOGGER.info("Translucent Leaves loaded on {}. Dynamic leaf light transparency active.", isClient ? "client" : "server");

        NeoForge.EVENT_BUS.register(this);
        unpackHelperMod();
    }

    @SubscribeEvent
    public void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            // Register world globally so helper mod can reset it if main mod gets uninstalled
            GlobalWorldTracker.markWorldAsModified(level);

            LeafLightSavedData data = LeafLightSavedData.get(level);

            if (!data.isProcessed()) {
                LOGGER.info("First load with Translucent Leaves detected on level {}. Triggering light updates...", level.dimension().location());
                recalculateLighting(level);
                data.setProcessed(true);
            }
        }
    }

    private void recalculateLighting(ServerLevel level) {
        for (ChunkHolder holder : getLoadedChunkHolders(level)) {
            LevelChunk chunk = holder.getTickingChunk();
            if (chunk != null) {
                BlockPos.betweenClosedStream(
                    chunk.getPos().getMinBlockX(), level.getMinBuildHeight(), chunk.getPos().getMinBlockZ(),
                    chunk.getPos().getMaxBlockX(), level.getMaxBuildHeight(), chunk.getPos().getMaxBlockZ()
                ).forEach(pos -> {
                    if (LeafLightCompat.shouldApply(level.getBlockState(pos).getBlock())) {
                        level.getChunkSource().getLightEngine().checkBlock(pos);
                    }
                });
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Iterable<ChunkHolder> getLoadedChunkHolders(ServerLevel level) {
        try {
            Method method = net.minecraft.server.level.ChunkMap.class.getDeclaredMethod("getChunks");
            method.setAccessible(true);
            return (Iterable<ChunkHolder>) method.invoke(level.getChunkSource().chunkMap);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private void unpackHelperMod() {
        try {
            Path modsFolder = FMLPaths.MODSDIR.get();
            Path helperJar = modsFolder.resolve("dont_delete_this_translucentleaves.jar");

            if (!Files.exists(helperJar)) {
                try (InputStream in = getClass().getResourceAsStream("/assets/translucentleaves/dont_delete_this_translucentleaves.jar")) {
                    if (in != null) {
                        Files.copy(in, helperJar, StandardCopyOption.REPLACE_EXISTING);
                        LOGGER.info("Unpacked TranslucentLeaves Helper Mod for uninstallation handling.");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to unpack Helper Mod:", e);
        }
    }
}