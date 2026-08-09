package cz.miki85.translucentleaves;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class LeafLightSavedData extends SavedData {
    private boolean processed = false;

    public static LeafLightSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(
                LeafLightSavedData::new,
                LeafLightSavedData::load,
                null
            ),
            "translucentleaves_light_data"
        );
    }

    public static LeafLightSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        LeafLightSavedData data = new LeafLightSavedData();
        data.processed = tag.getBoolean("processed");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putBoolean("processed", this.processed);
        return tag;
    }

    public boolean isProcessed() {
        return this.processed;
    }

    public void setProcessed(boolean value) {
        this.processed = value;
        this.setDirty();
    }
}