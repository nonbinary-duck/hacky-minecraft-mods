package hmm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


@Mixin(FarmlandBlock.class)
public abstract class SaferFarmland extends Block {

    public SaferFarmland(Settings settings) {
        super(settings);
    }

    @Inject(method = "onLandedUpon", at = @At(value = "HEAD"), cancellable = true)
    public void preventDirtification(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci)
    {
        if (!world.isClient)
        {
            // This isn't a player, cancel it
            if (!(entity instanceof PlayerEntity)) {
                super.onLandedUpon(world, state, pos, entity, fallDistance);
                
                ci.cancel();
            }
        }
        
        /*
        Ref. Code from 1.17.1

        public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
            if (
                !world.isClient &&
                world.random.nextFloat() < fallDistance - 0.5f &&
                entity instanceof LivingEntity && (
                    entity instanceof PlayerEntity ||
                    world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
                )
                && entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512f
            )
            {
                FarmlandBlock.setToDirt(state, world, pos);
            }
            super.onLandedUpon(world, state, pos, entity, fallDistance);
        }
        */
    }

    @ModifyConstant(method = "onLandedUpon", constant =  @Constant(floatValue = 0.5f))
    public float reduceDirtification(float original)
    {
        // Make is so a drop from 1 block is guaranteed to not convert it to dirt
        return 1f;
    }
}