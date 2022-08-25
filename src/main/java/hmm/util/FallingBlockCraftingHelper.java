package hmm.util;

import java.lang.reflect.Constructor;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;


public class FallingBlockCraftingHelper
{
    public static Block getRecipe(List<FallingBlockEntity> components)
    {
        return Blocks.BEDROCK;
    }

    public static FallingBlockEntity transformInto(List<FallingBlockEntity> components, Block result) throws Throwable
    {
        for (FallingBlockEntity fallingBlockEntity : components)
        {
            fallingBlockEntity.remove(RemovalReason.DISCARDED);
        }

        // "Transform" the remaining entity
        FallingBlockEntity template = components.get(0);

        if ( !( template.world instanceof ServerWorld ) ) return null;

        // Use the private constructor in FallingBlockEntity
        Constructor<FallingBlockEntity> privateCtor = FallingBlockEntity.class.getDeclaredConstructor(World.class, double.class, double.class, double.class, BlockState.class);

        privateCtor.setAccessible(true);

        // Create a new falling block
        FallingBlockEntity newFallingBlock = privateCtor.newInstance(
            template.world,
            template.getX(),
            template.getY(),
            template.getZ(),
            result.getDefaultState()
        );

        // Spawn it
        ((ServerWorld)template.world).spawnEntity(newFallingBlock);

        // Destroy the final component
        template.remove(RemovalReason.DISCARDED);

        return newFallingBlock;
    }
}
