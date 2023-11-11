package hmm.mixin;

import java.util.Comparator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlChunkLoading extends ThrownItemEntity
{
    private static final ChunkTicketType<ChunkPos> PEARL_CHUNK_LOADER_TICKET = ChunkTicketType.create("hmm_pearl_chunk_loader", Comparator.comparingLong(ChunkPos::toLong), 30);

    public EnderPearlChunkLoading(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void loadChunks(CallbackInfo ci)
    {
        // /data get entity @e[type=minecraft:ender_pearl, sort=nearest, limit=1] Owner
        // /summon minecraft:ender_pearl ~ ~3 ~ {Motion:[0d, 1d, 0d], Owner:[I; 1229857164, -314688488, -2018928863, 686132395]}
        // /data modify entity @e[type=minecraft:ender_pearl, sort=nearest, limit=1] Motion set value [0d, 0d, 1d]


        // vel is vec3d
                    // pos is is also vec3d
                    // so the block pos is the problem ????
                    // why did they remove a continent feature?
        Vec3d v = this.getVelocity().add(this.getPos());
        if (!this.getWorld().isClient())
        {
            // Calculate the next position and load it
            ChunkPos nextChunkPos = new ChunkPos(
                new BlockPos((int)Math.floor(v.x), (int)Math.floor(v.y), (int)Math.floor(v.z)) // We'll have to do it this way I guess...
                // Yes Java, I know it's a lossy conversion...
            );

            // System.out.println("Block x: " + nextPos.getX() + "z " + nextPos.getZ() + " Chunk x: " + cnext.x + "z " + cnext.z);
            
            // ChunkHelper.ForceLoadChunk(nextPos.getX(), nextPos.getZ(), 20, ((ServerWorld)this.getWorld()).getChunkManager());

            ((ServerWorld)this.getWorld()).getChunkManager().addTicket(
                // Our custom ticket with timeout of 30 ticks, technically same as "unknown", though this is probably better
                PEARL_CHUNK_LOADER_TICKET,
                // The chunk to load
                nextChunkPos,
                // The area to load
                // net/minecraft/server/world/ChunkTickManager;shouldTickEntities(J)Z checks if level is smaller than 32
                // net/minecraft/server/world/ChunkTickManager;addTicket(type, pos, radius, identifier)V sets the level to 33 - radius
                // This makes the level of the chunk loaded 31, so 31 < 32 == true therefore entities are ticked, whereas 1 would make it so only blocks are ticked
                2,
                // The identifier to give the ticket
                nextChunkPos
            );
        }
    }

}
