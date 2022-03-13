package hmm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hmm.util.ChunkHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlChunkLoading extends ThrownItemEntity {

    public EnderPearlChunkLoading(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void loadChunks(CallbackInfo ci)
    {
        // /data get entity @e[type=minecraft:ender_pearl, sort=nearest, limit=1] Owner
        // /summon minecraft:ender_pearl ~ ~3 ~ {Motion:[0d, 1d, 0d], Owner:[I; 1229857164, -314688488, -2018928863, 686132395]}
        if (!this.getWorld().isClient())
        {
            // Calculate the next position and load it
            BlockPos nextPos = new BlockPos(this.getVelocity().add(this.getPos()));
            
            ChunkHelper.ForceLoadChunk(nextPos.getX(), nextPos.getZ(), 20, ((ServerWorld)this.getWorld()).getChunkManager());
        }
    }

}
