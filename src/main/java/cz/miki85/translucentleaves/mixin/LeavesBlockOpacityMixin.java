package cz.miki85.translucentleaves.mixin;

import cz.miki85.translucentleaves.LeafLightCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class LeavesBlockOpacityMixin {

    // 1. Forces light occlusion/decay to be 0 (acts like air)
    @Inject(method = "getLightBlock", at = @At("HEAD"), cancellable = true)
    private void forceZeroOpacity(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        BlockState state = (BlockState) (Object) this;
        if (LeafLightCompat.shouldApply(state.getBlock())) {
            cir.setReturnValue(0);
        }
    }

    // 2. Forces sky light to pass straight down vertically without diminishing
    @Inject(method = "propagatesSkylightDown", at = @At("HEAD"), cancellable = true)
    private void forceSkylightPropagation(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = (BlockState) (Object) this;
        if (LeafLightCompat.shouldApply(state.getBlock())) {
            cir.setReturnValue(true);
        }
    }
}