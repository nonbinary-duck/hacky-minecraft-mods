package hmm.mixin.examples;

import java.util.LinkedList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.Explosion.DestructionType;


@Mixin(ChestBlockEntity.class)
public abstract class Injects extends LockableContainerBlockEntity {

    protected Injects(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "onClose", at = @At(value = "HEAD"))
    public void onCloseInject(PlayerEntity playerEntity, CallbackInfo ci)
    {
        if (this.getName().asString().toLowerCase().contains("boom"))
        {
            explodeChest(playerEntity);
        }
        else if (this.getName().asString().toLowerCase().contains("behind you!"))
        {
            spawnSpooker(playerEntity);
        }
    }

    private void explodeChest(PlayerEntity playerEntity)
    {
        BlockPos bp = this.getPos();
        Vec3d pos = new Vec3d(bp.getX() - 0.5, bp.getY() + 0.5, bp.getZ() - 0.5);
        
        // Create an entity to attribute the explosion to
        Entity chest = new ZombieEntity(world);

        chest.setCustomName(this.getName());
        chest.setPos(pos.x, pos.y, pos.z);

        // Send the player a message
        playerEntity.sendMessage(new LiteralText("[" + chest.getName().asString() + "] BOOM!"), false);

        // Set the name to be something else
        setCustomName(new TranslatableText("container.chest"));
        
        // Explode!
        world.createExplosion(chest, DamageSource.MAGIC, new ExplosionBehavior(), pos.x, pos.y, pos.z, 8.5f, false, Explosion.DestructionType.BREAK);
    }

    private void spawnSpooker(PlayerEntity playerEntity)
    {
        ZombieEntity spooker = new ZombieEntity(world);

        Vec3d pos = playerEntity.getPos();
        
        // Get the polar vec of where the player is looking
        Vec3d polar = Vec3d.fromPolar(playerEntity.getPitch(), playerEntity.getYaw());
        
        // Make it a unit vector
        polar = polar.multiply(Math.pow(polar.length(), -1D));
        
        // Make it 4 blocks projected away from where the player is looking
        pos = pos.add(polar.multiply(-4));

        // Set them to the correct position
        spooker.setPos(pos.x, pos.y, pos.z);
        
        // Make them look at the player
        spooker.lookAt(EntityAnchor.EYES, playerEntity.getPos());

        // Set their name
        spooker.setCustomName(new LiteralText("BOO!"));

        // Set their health to 2
        spooker.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(2.0);
        spooker.setHealth(2.0f);
        
        // Spawn the zombie
        world.spawnEntity(spooker);
        
        // Fixes the whole floating in mid-air thing
        spooker.tick();
    }
}