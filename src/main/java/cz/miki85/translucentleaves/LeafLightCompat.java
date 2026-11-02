package cz.miki85.translucentleaves;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.BlockTags;

public final class LeafLightCompat {
    private LeafLightCompat() {
    }

    public static boolean shouldApply(Block block) {
        return block != null && (block == Blocks.SNOW || block.defaultBlockState().is(BlockTags.LEAVES));
    }

    public static boolean shouldApplyName(String blockName) {
        if (blockName == null) {
            return false;
        }

        String normalized = blockName.toLowerCase();
        return normalized.contains("leaves") || normalized.contains("leaf");
    }
}