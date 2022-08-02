package hmm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

@Mixin(ProjectileEntity.class)
public abstract class AccurateProjectiles extends Entity
{
    public AccurateProjectiles(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyVariable(method = "setVelocity", at = @At(value = "LOAD"), argsOnly = true, ordinal = 1)
    private float modifyDivergence(float original)
    {
        // If it's a player and an ender pearl, set the accuracy to 50%
        if (original == 1.0f && this.getType() == EntityType.ENDER_PEARL) return 0.5f;
        // If it's a player and an arrow, increase the accuracy by 25%
        if (original == 1.0f && this.getType() == EntityType.ARROW) return 0.75f;
        // Otherwise, use whatever
        return original;
    }
}
