package hmm.mixin.shenanigans;

import java.util.LinkedList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hmm.util.FallingBlockCraftingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;


@Mixin(PistonBlockEntity.class)
public abstract class FallingBlockCrafting extends BlockEntity
{

    public FallingBlockCrafting(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
        method = "pushEntities",
        at = @At(//"RETURN"))
            // Inject our code only after checking this b32 can collide with stuff and there are definitely entities within its bounding box
            value = "INVOKE",
            target = "Lnet/minecraft/util/shape/VoxelShape;getBoundingBoxes()Ljava/util/List;",
            // target = "Lnet/minecraft/block/AbstractBlock$AbstractBlockState;isOf(Lnet/minecraft/block/Block;)Z",
            ordinal = 0,
            shift = At.Shift.BEFORE))
            // Won't throw throwable, java is annoying
    private static void onPushEntities(World world, BlockPos pos, float f, PistonBlockEntity blockEntity, CallbackInfo ci) throws Throwable
    {
        // Only allow recipes for slime blocks
        if (!blockEntity.getPushedBlock().isOf(Blocks.SLIME_BLOCK)) return;
        if (!world.isClient()) return;

        Box pushBox = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

        List<FallingBlockEntity> fallingBlocks = world.getEntitiesByClass(
            FallingBlockEntity.class,
            pushBox,
            ent -> (true)
        );

        // Require at least 2 falling blocks
        if (fallingBlocks.size() < 2) return;
        
        // Get the fist element to compare to the others
        FallingBlockEntity ent = fallingBlocks.get(0);
        
        // Check that all of the entities are all compatible (also adds the first element since we didn't set it to null)
        List<FallingBlockEntity> components = getCompatibleEntities(ent, fallingBlocks);
        
        // If not all of the elements are compatible, stop
        if (components.size() != fallingBlocks.size()) return;

        System.out.println("Group formed: " + components.size());

        Block recipe = FallingBlockCraftingHelper.getRecipe(components);

        FallingBlockCraftingHelper.transformInto(components, recipe);
    }

    private static List<FallingBlockEntity> getCompatibleEntities(FallingBlockEntity entity, List<FallingBlockEntity> fallingBlocks)
    {
        List<FallingBlockEntity> compatibleList = new LinkedList<FallingBlockEntity>();

        for (int i = 0; i < fallingBlocks.size(); i++)
        {
            FallingBlockEntity fallingBlockEntity = fallingBlocks.get(i);

            if (fallingBlockEntity == null) continue;
            
            System.out.println("\n0: " + fallingBlockEntity.getPos().distanceTo(entity.getPos()) + "\n" + "1: " + fallingBlockEntity.getVelocity().distanceTo(entity.getVelocity()) + "\n" + (fallingBlockEntity.getPos().distanceTo(entity.getPos()) == 0) + " " + (fallingBlockEntity.getVelocity().distanceTo(entity.getVelocity()) == 0 ));

            if (
                fallingBlockEntity.getPos().distanceTo(entity.getPos()) == 0 &&
                fallingBlockEntity.getVelocity().distanceTo(entity.getVelocity()) == 0)
            {
                // Add our element to the list
                compatibleList.add(fallingBlockEntity);
                // Remove it from candidates and make sure we go to the next element ok
                fallingBlocks.set(i, null);
            }
        }

        return compatibleList;
    }
}
