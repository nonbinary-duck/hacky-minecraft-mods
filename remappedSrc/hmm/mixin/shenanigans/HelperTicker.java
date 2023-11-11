package hmm.mixin.shenanigans;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hmm.util.ShenanigansHelper;
import net.minecraft.server.MinecraftServer;


@Mixin(MinecraftServer.class)
public abstract class HelperTicker
{
    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At("HEAD"))
    public void beforeTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
    {
        ShenanigansHelper.OnTickBegin();
    }
}
