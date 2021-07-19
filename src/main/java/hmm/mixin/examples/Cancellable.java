package hmm.mixin.examples;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.util.math.Direction;


@Mixin(EndPortalBlockEntity.class)
public class Cancellable {
    // Makes the end portals look like gateways (full block)
    // An example of a client-only inject
    @Environment(EnvType.CLIENT)
    @Inject(method = "shouldDrawSide", at = @At(value = "HEAD"), cancellable = true)
    public void shouldDrawSide (Direction direction, CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(true);
    }
}
