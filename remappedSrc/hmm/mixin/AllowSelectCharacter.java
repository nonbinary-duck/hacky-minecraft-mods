package hmm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import net.minecraft.SharedConstants;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;


@Mixin(SharedConstants.class)
public abstract class AllowSelectCharacter
{

    @Inject(
        method = "isValidChar",
        at = @At("HEAD"),
        cancellable = true)
    private static void replaceSelectCharacter(char chr, CallbackInfoReturnable<Boolean> ci)
    {
        if (chr == '\u00a7') ci.setReturnValue(true);
        Xoroshiro128PlusPlusRandom a = new Xoroshiro128PlusPlusRandom(550);

        
    }
}
