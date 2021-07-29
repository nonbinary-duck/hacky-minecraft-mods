package hmm.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;


@Mixin(ItemEntity.class)
public abstract class DespawningItemsWarning extends Entity {
    
    public abstract @Shadow ItemStack getStack();

    public DespawningItemsWarning(EntityType<? extends ItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V", ordinal = 1))
    public void onRemoveByTimeInject(CallbackInfo ci)
    {
        List<? extends PlayerEntity> players = world.getPlayers();
        
        for (PlayerEntity player : players) {
            player.sendMessage(
                new LiteralText(
                    String.format(
                        "Item (%d x %s) was killed from timeout",
                        getStack().getCount(),
                        getStack().getItem().getName().getString()
                    )
                )
                , false
            );
        }
    }
}