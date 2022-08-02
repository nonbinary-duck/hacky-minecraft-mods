package hmm.mixin.examples;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class AfterServerTick
{
    // After the server has finished ticking, do our stuff
    @Inject(method = "tick", at = @At(value = "RETURN"))
    public void tickHook(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
    {
        // ChunkHelper.TickForceLoadedChunks();
    }
}
