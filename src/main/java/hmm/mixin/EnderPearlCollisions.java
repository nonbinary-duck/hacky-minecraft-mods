package hmm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.world.World;


@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlCollisions extends ThrownItemEntity {

    public EnderPearlCollisions(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onCollision", at = @At(value = "HEAD"), cancellable = true)
    public void cancelCollisions(HitResult hitResult, CallbackInfo ci)
    {
        if (hitResult.getType() == Type.ENTITY)
        {
            
            // Cancel collisions with living entities
            if (
                !(
                    ((EntityHitResult)hitResult)
                        .getEntity() instanceof LivingEntity
                    )
                )
            {
                ci.cancel();
            }
        }
    }

}
