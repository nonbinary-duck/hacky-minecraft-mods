package hmm.util;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;


public class FallingBlockCraftingHelper
{
    public static Block getRecipe(List<FallingBlockEntity> components)
    {
        return Blocks.BEDROCK;
    }

    public static FallingBlockEntity transformInto(List<FallingBlockEntity> components, Block result) throws Throwable
    {
        // Transform the remaining entity
        FallingBlockEntity ent = components.get(0);

        if ( !( ent.world instanceof ServerWorld ) ) return null;

        // Get the private field which stores the block data
        Field block = FallingBlockEntity.class.getDeclaredField("block");
        // Field block = FallingBlockEntity.class.getDeclaredField("field_7188");

        // Give us access to it and change it
        block.setAccessible(true);
        block.set(ent, result.getDefaultState());

        return ent;
    }
}
