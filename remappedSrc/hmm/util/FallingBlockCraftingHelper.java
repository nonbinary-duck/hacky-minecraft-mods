package hmm.util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;


public class FallingBlockCraftingHelper
{
    public static Block getRecipe(List<FallingBlockEntity> components)
    {
        Vector<Block> blocks = new Vector<Block>(components.size());
        
        int i = 0;
        boolean isBedrockInvolved = false;
        
        // Convert the list of falling blocks to a vector of blocks
        for (FallingBlockEntity fallingBlockEntity : components)
        {
            blocks.add(fallingBlockEntity.getBlockState().getBlock());

            if (blocks.get(i++) == Blocks.BEDROCK) isBedrockInvolved = true;
        }

        // Any recipe with bedrock produces a b32
        if (isBedrockInvolved) return Blocks.MOVING_PISTON;
        
        // Loop over all of the recipes
        for (Recipe recipe : RECIPES)
        {
            // Check there are the same number of components
            if (recipe.components.length != blocks.size()) continue;

            // Check that the components match
            int matches = 0;

            // For every block, check if any blocks match
            // This makes it shapeless
            for (Block component : blocks)
            {
                for (Block recipeComponent : recipe.components)
                {
                    // If the block matches one of the components, we've found a match
                    if (component == recipeComponent) matches++;
                }
            }
            
            if (matches == blocks.size())
            {
                return recipe.result;
            }
        }


        // If no recipe was found, return null
        return null;
    }

    public static FallingBlockEntity transformInto(List<FallingBlockEntity> components, Block result) throws Throwable
    {
        // Transform the remaining entity
        FallingBlockEntity ent = components.get(0);

        if ( !( ent.world instanceof ServerWorld ) ) return null;

        // Get the private field which stores the block data
        Field block = FallingBlockEntity.class.getDeclaredFields()[1];
        // Field block = FallingBlockEntity.class.getDeclaredField("block");
        // Field block = FallingBlockEntity.class.getDeclaredField("field_7188");

        // Give us access to it and change it
        block.setAccessible(true);

        block.set(ent, result.getDefaultState());

        return ent;
    }

    public static class Recipe
    {
        public Recipe(Block result, Block... components) { this.result = result; this.components = components; }
        
        public Block result;
        
        public Block[] components;
    }
    
    public static final Recipe[] RECIPES = {
        new Recipe(Blocks.LIGHT,                            Blocks.OAK_SLAB, Blocks.SPONGE),
        new Recipe(Blocks.SPAWNER,                          Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.GREEN_STAINED_GLASS_PANE),
        new Recipe(Blocks.PLAYER_HEAD,                      Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_STONE_BRICKS),
        new Recipe(Blocks.PISTON_HEAD,                      Blocks.RED_SANDSTONE_WALL, Blocks.BLACK_STAINED_GLASS_PANE),
        new Recipe(Blocks.BUDDING_AMETHYST,                 Blocks.ORANGE_WOOL, Blocks.DEEPSLATE),
        new Recipe(Blocks.PETRIFIED_OAK_SLAB,               Blocks.ANCIENT_DEBRIS, Blocks.OAK_PLANKS),
        
        new Recipe(Blocks.BEDROCK,                          Blocks.SANDSTONE_WALL, Blocks.STRIPPED_DARK_OAK_WOOD),
        new Recipe(Blocks.BARRIER,                          Blocks.WARPED_SLAB, Blocks.WHITE_TERRACOTTA),
        new Recipe(Blocks.REINFORCED_DEEPSLATE,             Blocks.JUNGLE_FENCE_GATE, Blocks.COAL_BLOCK),

        new Recipe(Blocks.COMMAND_BLOCK,                    Blocks.RED_SANDSTONE_STAIRS, Blocks.AMETHYST_BLOCK),
        new Recipe(Blocks.REPEATING_COMMAND_BLOCK,          Blocks.HONEYCOMB_BLOCK, Blocks.BOOKSHELF),
        new Recipe(Blocks.CHAIN_COMMAND_BLOCK,              Blocks.TARGET, Blocks.DEEPSLATE_GOLD_ORE),

        new Recipe(Blocks.STRUCTURE_BLOCK,                  Blocks.PURPLE_STAINED_GLASS, Blocks.BOOKSHELF),
        new Recipe(Blocks.STRUCTURE_VOID,                   Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CUT_RED_SANDSTONE_SLAB),
        new Recipe(Blocks.JIGSAW,                           Blocks.CRIMSON_TRAPDOOR, Blocks.DEEPSLATE_BRICK_WALL),

        new Recipe(Blocks.END_PORTAL,                       Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.SPRUCE_LOG),
        new Recipe(Blocks.END_GATEWAY,                      Blocks.DARK_OAK_PLANKS, Blocks.CRIMSON_HYPHAE),
        new Recipe(Blocks.END_PORTAL_FRAME,                 Blocks.MYCELIUM, Blocks.DEEPSLATE_TILE_STAIRS),
        new Recipe(Blocks.NETHER_PORTAL,                    Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.DARK_OAK_PLANKS),

        new Recipe(Blocks.FARMLAND,                         Blocks.REDSTONE_LAMP, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE),
        new Recipe(Blocks.DIRT_PATH,                        Blocks.BRICK_SLAB, Blocks.MUD_BRICKS),
        new Recipe(Blocks.TALL_GRASS,                       Blocks.MOSSY_COBBLESTONE, Blocks.YELLOW_STAINED_GLASS),
        new Recipe(Blocks.LARGE_FERN,                       Blocks.QUARTZ_SLAB, Blocks.BASALT),
        // new Recipe(Blocks.TALL_SEAGRASS,                    Blocks.MUD, Blocks.WHITE_CONCRETE),

        new Recipe(Blocks.WATER,                            Blocks.REDSTONE_ORE, Blocks.BLUE_ICE),
        new Recipe(Blocks.LAVA,                             Blocks.DEEPSLATE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_STAIRS),
        new Recipe(Blocks.FIRE,                             Blocks.TARGET, Blocks.ORANGE_WOOL),
        new Recipe(Blocks.SOUL_FIRE,                        Blocks.PRISMARINE_SLAB, Blocks.GILDED_BLACKSTONE),

        new Recipe(Blocks.INFESTED_CHISELED_STONE_BRICKS,   Blocks.QUARTZ_STAIRS, Blocks.BRICK_WALL, Blocks.STONE_BRICK_WALL),
        new Recipe(Blocks.INFESTED_COBBLESTONE,             Blocks.QUARTZ_STAIRS, Blocks.BRICK_WALL, Blocks.LIME_CONCRETE),
        new Recipe(Blocks.INFESTED_CRACKED_STONE_BRICKS,    Blocks.QUARTZ_STAIRS, Blocks.BRICK_WALL, Blocks.DIORITE_STAIRS),
        new Recipe(Blocks.INFESTED_DEEPSLATE,               Blocks.QUARTZ_STAIRS, Blocks.BRICK_WALL, Blocks.PINK_CONCRETE),
        new Recipe(Blocks.INFESTED_MOSSY_STONE_BRICKS,      Blocks.QUARTZ_STAIRS, Blocks.BRICK_WALL, Blocks.END_STONE),
        new Recipe(Blocks.INFESTED_STONE,                   Blocks.QUARTZ_STAIRS, Blocks.BRICK_WALL, Blocks.NETHER_BRICKS),
        new Recipe(Blocks.INFESTED_STONE_BRICKS,            Blocks.QUARTZ_STAIRS, Blocks.BRICK_WALL, Blocks.WAXED_CUT_COPPER),
    };
}
