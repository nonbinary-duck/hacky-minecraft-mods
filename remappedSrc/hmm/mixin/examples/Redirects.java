package hmm.mixin.examples;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.resource.SplashTextResourceSupplier;

@Mixin(SplashTextResourceSupplier.class)
public abstract class Redirects {

    // (never) Return 42 when random is called first
    // (never) Shows "<playerName> IS YOU"
    @Redirect(method = "get", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0))
    public int randomHack_1(Random rnd, int max) {
        return 0;
    }

    // Return 42 when random is called second
    // Always shows the 43rd splash text
    @Redirect(method = "get", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 1))
    public int randomHack_2(Random rnd, int max) {
        return 42;
    }
}
