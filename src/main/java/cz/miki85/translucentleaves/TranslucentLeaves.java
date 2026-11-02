package cz.miki85.translucentleaves;

import com.mojang.logging.LogUtils;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkDataEvent;
import org.slf4j.Logger;

@Mod("translucentleaves")
public class TranslucentLeaves {
    private static final Logger LOGGER = LogUtils.getLogger();

    public TranslucentLeaves() {
        boolean isClient = FMLLoader.getDist() == Dist.CLIENT;
        LOGGER.info("Translucent Leaves loaded on {}. Dynamic leaf light transparency active.", isClient ? "client" : "server");

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onChunkDataLoad(ChunkDataEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ChunkAccess chunk = event.getChunk();
        CompoundTag data = event.getData();
        data.remove("isLightOn");

        ListTag sections = data.getList("sections", Tag.TAG_COMPOUND);
        for (int i = 0; i < sections.size(); i++) {
            CompoundTag section = sections.getCompound(i);
            section.remove("BlockLight");
            section.remove("SkyLight");
        }

        chunk.setLightCorrect(false);
        LevelLightEngine lightEngine = level.getChunkSource().getLightEngine();
        ChunkPos chunkPos = chunk.getPos();
        lightEngine.retainData(chunkPos, false);

        for (int sectionY = lightEngine.getMinLightSection(); sectionY < lightEngine.getMaxLightSection(); sectionY++) {
            SectionPos sectionPos = SectionPos.of(chunkPos, sectionY);
            lightEngine.queueSectionData(LightLayer.BLOCK, sectionPos, null);
            lightEngine.queueSectionData(LightLayer.SKY, sectionPos, null);
        }
    }
}