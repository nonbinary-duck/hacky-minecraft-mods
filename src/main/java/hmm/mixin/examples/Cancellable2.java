package hmm.mixin.examples;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;


@Mixin(Entity.class)
public abstract class Cancellable2 {
    Random rnd = new Random();
    
    // Makes mobs flicker in an out of existence as they get either further or closer
    // This is horrible
    // Also an example of a client-only inject in mixins.json
    @Environment(EnvType.CLIENT)
    @Inject(method = "shouldRender(D)Z", at = @At(value = "HEAD"), cancellable = true)
    public void shouldRender(double distance, CallbackInfoReturnable<Boolean> ci) {
        double factor = rnd.nextDouble() * 4096;
        
        // Disabled because I'm not insane
        if (false) ci.setReturnValue(!((distance > factor) || (distance * rnd.nextDouble() * 80 < factor)));
    }
}
