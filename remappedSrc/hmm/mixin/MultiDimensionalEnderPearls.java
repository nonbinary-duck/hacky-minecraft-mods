package hmm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;


@Mixin(EnderPearlEntity.class)
public abstract class MultiDimensionalEnderPearls extends ThrownItemEntity {

    public MultiDimensionalEnderPearls(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onCollision", at = @At(value = "HEAD"), cancellable = true)
    public void transferPlayer(HitResult res, CallbackInfo ci)
    {
        // Check basic stuff
        if (this.isRemoved() || this.world.isClient()) return;
        if ( !( this.getOwner() instanceof ServerPlayerEntity ) ) return;

        // Get information and cast it to its correct type
        ServerWorld serverWorld   = (ServerWorld)this.getWorld();
        ServerPlayerEntity player = (ServerPlayerEntity)this.getOwner();

        // Check that the player is in another dimension, and therefore our routine is needed
        if (serverWorld.getDimension().equals(player.getWorld().getDimension())) return;

        // Check that the teleport would have otherwise gone through
        if (player.networkHandler.getConnection().isOpen() && !player.isSleeping())
        {
            // The normal ender pearl algorithm

            // Create the endermite
            if (this.random.nextFloat() < 0.05f && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING))
            {
                EndermiteEntity lv2 = EntityType.ENDERMITE.create(this.world);
                lv2.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                this.world.spawnEntity(lv2);
            }

            // Teleport as the normal ways, except change the world first
            if (player.hasVehicle())
            {
                player.moveToWorld(serverWorld);
                player.requestTeleportAndDismount(this.getX(), this.getY(), this.getZ());
            }
            else
            {
                player.moveToWorld(serverWorld);
                player.requestTeleport(this.getX(), this.getY(), this.getZ());
            }
            
            // Trigger an event and apply damage, such as the normal way
            player.onLanding();
            player.damage(DamageSource.FALL, 5.0f);
            
            // Cancel the normal algorithm
            ci.cancel();
        }
    }

}
