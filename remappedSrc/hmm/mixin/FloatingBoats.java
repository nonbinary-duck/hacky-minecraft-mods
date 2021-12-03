package hmm.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.BoatEntity.Location;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


@Mixin(BoatEntity.class)
public abstract class FloatingBoats extends Entity
{
    // 5.333 m/s max
    private float groundFriction = 0.85f;
    // 11.000 m/s max is 0.92727
    // 10.000 m/s max is 0.92
    private float waterFriction = 0.92f;
    
    private @Shadow float velocityDecay;
    private @Shadow Location location;
    private @Shadow Location lastLocation;
    private @Shadow float field_7714;
    protected abstract @Shadow Location getUnderWaterLocation();
    
    public FloatingBoats(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Redirect(method = "updateVelocity()V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/BoatEntity;velocityDecay:F", opcode = Opcodes.GETFIELD))
    private float ChangeVelocityDecay(BoatEntity boatEntity)
    {
        if (this.location == Location.IN_WATER)
        {
            return this.waterFriction;
        }
        
        return this.velocityDecay;
    }
    
    @Inject(method = "method_7548()F", at = @At(value = "RETURN"), cancellable = true)
    private void speedyBoat(CallbackInfoReturnable<Float> ci)
    {
        Float rv = ci.getReturnValue();

        if (Float.isNaN(rv)) return;

        if (this.location == Location.ON_LAND)
        {
            if (rv > groundFriction) return;
    
            ci.setReturnValue(groundFriction);
        }
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