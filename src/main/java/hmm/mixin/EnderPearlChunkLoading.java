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
        ///summon minecraft:ender_pearl ~ ~3 ~ {Motion:[9d, 1d, 0d], Owner:[I; 706207697, 312292107, -1632308381, -284807443]}
        if (!this.getWorld().isClient())
        {
            // Calculate the next position and load it
            BlockPos nextPos = new BlockPos(this.getVelocity().add(this.getPos()));

            // System.out.println("Pearl ticking, next pos will be x: " + nextPos.getX() + " z: " + nextPos.getZ());
            
            ChunkHelper.ForceLoadChunk(nextPos.getX(), nextPos.getZ(), 20, ((ServerWorld)this.getWorld()).getChunkManager());
        }
    }

}
