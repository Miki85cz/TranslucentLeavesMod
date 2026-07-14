package cz.miki85.translucentleaves;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;

public final class LeafLightCompat {
    private LeafLightCompat() {
    }

    public static boolean shouldApply(Block block) {
        return block instanceof LeavesBlock;
    }

    public static boolean shouldApplyName(String blockName) {
        if (blockName == null) {
            return false;
        }

        String normalized = blockName.toLowerCase();
        return normalized.contains("leaves") || normalized.contains("leaf");
    }
}
