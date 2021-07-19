package hmm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.BoatEntity.Location;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


@Mixin(BoatEntity.class)
public abstract class FloatingBoats extends Entity
{
    private final float GROUND_MIN_SLIP = 0.85f;
    
    private @Shadow Location location;
    private @Shadow Location lastLocation;
    private @Shadow float field_7714;
    // public abstract @Shadow Vec3d getVelocity();
    protected abstract @Shadow Location getUnderWaterLocation();
    
    public FloatingBoats(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Inject(method = "method_7548()F", at = @At(value = "RETURN"), cancellable = true)
    private void speedyBoat(CallbackInfoReturnable<Float> ci)
    {
        Float rv = ci.getReturnValue();

        if (Float.isNaN(rv)) return;
        
        if (rv > GROUND_MIN_SLIP) return;

        ci.setReturnValue(GROUND_MIN_SLIP);
    }

    @Inject(method = "updateVelocity()V", at = @At(value = "RETURN"))
    private void pushUpFromWater(CallbackInfo ci)
    {
        if (this.location == Location.UNDER_WATER || this.location == Location.UNDER_FLOWING_WATER)
        {
            final Vec3d vel = this.getVelocity();
            
            this.setVelocity(vel.x, 0.15, vel.z);
        }
    }
}