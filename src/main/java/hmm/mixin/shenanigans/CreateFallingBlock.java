package hmm.mixin.shenanigans;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hmm.util.ShenanigansHelper;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;


@Mixin(PistonBlock.class)
public abstract class CreateFallingBlock extends FacingBlock
{
    protected CreateFallingBlock(Settings settings) { super(settings); }

    @Inject(
        method = "move(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Z",
        at = @At(value = "HEAD"),
        cancellable = true)
    public void moveInject(World world, BlockPos pos, Direction dir, boolean retract, CallbackInfoReturnable<Boolean> ci)
    {
        // Only modify the behaviour if funky behaviour has been triggered this tick
        if (!ShenanigansHelper.allowFunkyBehaviour) return;
        
        // Make sure the world is a serverWorld so we can modify stuff
        if ( !( world instanceof ServerWorld )) return;

        // Get the position of the block the piston is directly affecting
        BlockPos pushPos = pos.add(dir.getVector().multiply(2));
        // Get the block above that
        BlockPos abovePush = pushPos.add(0, 1, 0);

        // Require a glass pane to be underneath a falling block
        if ( !( world.getBlockState(pushPos).getBlock() instanceof PaneBlock ) ) return;

        // Search for a block 32 above the glass pane
        BlockEntity b32 = world.getBlockEntity(abovePush);
        if ( !( b32 instanceof PistonBlockEntity ) ) return;

        // Cast it to an actual block 32
        PistonBlockEntity targetBlock = (PistonBlockEntity)b32;

        // Then, search around to see if there's a falling block to replace with our b32
        PistonBlockEntity fallingBlock = getAdjacentThing(abovePush, world);
        
        // Check to see if there is a falling block to replace
        if (fallingBlock == null) return;

        // Cast the world to a serverWorld (we confirmed this earlier)
        ServerWorld sWorld = (ServerWorld)world;

        // Create a falling block with most of the position of the falling block and the block and data from the solid block
        // This method calls world.spawnEntity()
        FallingBlockEntity.spawnFromBlock(sWorld, fallingBlock.getPos(), targetBlock.getPushedBlock());
        
        // Destroy the falling block and the replacement block
        sWorld.removeBlockEntity(abovePush);
        sWorld.removeBlock(abovePush, false);
        sWorld.removeBlockEntity(fallingBlock.getPos());
        sWorld.removeBlock(fallingBlock.getPos(), false);

        // Cancel the event
        ci.setReturnValue(false);
    }

    protected PistonBlockEntity getAdjacentThing(BlockPos pos, World world)
    {
        // Search up, down, left, right, forward, backward

        for (int i = 0; i < DIRECTIONS.length; i++)
        {
            PistonBlockEntity pEnt = isThereThingIWant(pos, world, DIRECTIONS[i]);

            if (pEnt != null) return pEnt;
        }

        return null;
    }

    private PistonBlockEntity isThereThingIWant(BlockPos pos, World world, Direction dir)
    {
        // Get an entity at coords 1 in the direction specified
        BlockEntity ent = world.getBlockEntity(pos.add(dir.getVector()));

        // Check that it exists and it's b32
        if (ent instanceof PistonBlockEntity)
        {
            // Cast to a piston entity
            PistonBlockEntity pEnt = (PistonBlockEntity)ent;

            // Check that the block contained in the entity is a falling block
            if (pEnt.getPushedBlock().getBlock() instanceof FallingBlock)
            {
                // Check that it's headded in the opposite direction specified
                // i.e. it's just been pushed out of the position the other block was in
                if (pEnt.getMovementDirection() == dir) return pEnt;
            }
        }

        return null;
    }
}


/**
 * net/minecraft/block/PistonBlock
 * move
 * (Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Z
 * 
 * private boolean move(World world, BlockPos pos, Direction dir, boolean retract) {
        int l;
        BlockPos lv14;
        BlockPos lv6;
        int k;
        PistonHandler lv2;
        BlockPos lv = pos.offset(dir);
        if (!retract && world.getBlockState(lv).isOf(Blocks.PISTON_HEAD)) {
            world.setBlockState(lv, Blocks.AIR.getDefaultState(), 20);
        }
        if (!(lv2 = new PistonHandler(world, pos, dir, retract)).calculatePush()) {
            return false;
        }
        HashMap<BlockPos, BlockState> map = Maps.newHashMap();
        List<BlockPos> list = lv2.getMovedBlocks();
        ArrayList<BlockState> list2 = Lists.newArrayList();
        for (int i = 0; i < list.size(); ++i) {
            BlockPos lv3 = list.get(i);
            BlockState lv4 = world.getBlockState(lv3);
            list2.add(lv4);
            map.put(lv3, lv4);
        }
        List<BlockPos> list3 = lv2.getBrokenBlocks();
        BlockState[] lvs = new BlockState[list.size() + list3.size()];
        Direction lv5 = retract ? dir : dir.getOpposite();
        int j = 0;
        for (k = list3.size() - 1; k >= 0; --k) {
            lv6 = list3.get(k);
            BlockState class_26802 = world.getBlockState(lv6);
            BlockEntity lv8 = class_26802.hasBlockEntity() ? world.getBlockEntity(lv6) : null;
            PistonBlock.dropStacks(class_26802, world, lv6, lv8);
            world.setBlockState(lv6, Blocks.AIR.getDefaultState(), 18);
            world.emitGameEvent(GameEvent.BLOCK_DESTROY, lv6, GameEvent.Emitter.of(class_26802));
            if (!class_26802.isIn(BlockTags.FIRE)) {
                world.addBlockBreakParticles(lv6, class_26802);
            }
            lvs[j++] = class_26802;
        }
        for (k = list.size() - 1; k >= 0; --k) {
            lv6 = list.get(k);
            BlockState class_26803 = world.getBlockState(lv6);
            lv6 = lv6.offset(lv5);
            map.remove(lv6);
            BlockState lv9 = (BlockState)Blocks.MOVING_PISTON.getDefaultState().with(FACING, dir);
            world.setBlockState(lv6, lv9, 68);
            world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(lv6, lv9, (BlockState)list2.get(k), dir, retract, false));
            lvs[j++] = class_26803;
        }
        if (retract) {
            PistonType lv10 = this.sticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState lv11 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, dir)).with(PistonHeadBlock.TYPE, lv10);
            BlockState class_26804 = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, dir)).with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(lv);
            world.setBlockState(lv, class_26804, 68);
            world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(lv, class_26804, lv11, dir, true, true));
        }
        BlockState lv12 = Blocks.AIR.getDefaultState();
        for (BlockPos class_23382 : map.keySet()) {
            world.setBlockState(class_23382, lv12, 82);
        }
        for (Map.Entry entry : map.entrySet()) {
            lv14 = (BlockPos)entry.getKey();
            BlockState lv15 = (BlockState)entry.getValue();
            lv15.prepare(world, lv14, 2);
            lv12.updateNeighbors(world, lv14, 2);
            lv12.prepare(world, lv14, 2);
        }
        j = 0;
        for (l = list3.size() - 1; l >= 0; --l) {
            BlockState class_26805 = lvs[j++];
            lv14 = list3.get(l);
            class_26805.prepare(world, lv14, 2);
            world.updateNeighborsAlways(lv14, class_26805.getBlock());
        }
        for (l = list.size() - 1; l >= 0; --l) {
            world.updateNeighborsAlways(list.get(l), lvs[j++].getBlock());
        }
        if (retract) {
            world.updateNeighborsAlways(lv, Blocks.PISTON_HEAD);
        }
        return true;
    }
 */