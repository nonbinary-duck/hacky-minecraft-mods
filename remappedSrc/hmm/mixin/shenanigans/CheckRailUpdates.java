package hmm.mixin.shenanigans;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hmm.util.ShenanigansHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


@Mixin(PoweredRailBlock.class)
public abstract class CheckRailUpdates extends AbstractBlock
{

    public CheckRailUpdates(Settings settings) {
        super(settings);
    }
    
    @Inject(
        method = "updateBlockState(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V",
        at = @At("HEAD"))
    private void checkUpdateBlockState(BlockState state, World world, BlockPos pos, Block neighbour, CallbackInfo ci)
    {
        if (world.isClient()) return;
        
        // Record if a glass block has been retracted
        if (neighbour instanceof GlassBlock) ShenanigansHelper.glassBlockRemovedFromPoweredRail = true;
        
        // Only process updates for rails turning off since that behaviour is caused by budded rails
        // Increase the number of rail updates for this tick
        if (!state.get(PoweredRailBlock.POWERED)) ShenanigansHelper.IncrementRailUpdateTicker();
    }
}
