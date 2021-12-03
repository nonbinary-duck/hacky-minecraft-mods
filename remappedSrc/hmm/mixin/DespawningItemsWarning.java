package hmm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hmm.util.SendMessages;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
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
        SendMessages.sendMessageToAll(this.world,
            String.format(
                "Item (%d x %s) was killed from timeout",
                getStack().getCount(),
                getStack().getItem().getName().getString()
            )
        );
    }
}