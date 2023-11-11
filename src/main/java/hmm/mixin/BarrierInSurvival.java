package hmm.mixin;

import java.util.Set;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.registry.DynamicRegistryManager;


@Mixin(ClientWorld.class)
public abstract class BarrierInSurvival extends World
{
    //  protected World(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {

    
    protected BarrierInSurvival(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates); }

    // protected BarrierInSurvival(MutableWorldProperties properties, RegistryKey<World> registryRef,
    //         RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld,
    //         long seed, int maxChainedNeighborUpdates) {
    //     super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates); }

    @Shadow
    private MinecraftClient client;

    @Shadow
    private static Set<Item> BLOCK_MARKER_ITEMS;
    
    @Inject(
        method = "getBlockParticle",
        at = @At("HEAD"),
        cancellable = true)
    private void allowAnyGameMode(CallbackInfoReturnable<Block> ci)
    {
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.SURVIVAL)
        {
            // Slightly modified copy-paste from target method
            Item item;
            
            if (BLOCK_MARKER_ITEMS.contains(item = (this.client.player.getMainHandStack()).getItem()) && item instanceof BlockItem)
            {
                ci.setReturnValue( ((BlockItem)item).getBlock() );
            }
        }
    }
}
