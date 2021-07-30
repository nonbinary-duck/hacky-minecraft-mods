package hmm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hmm.util.SendMessages;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;


@Mixin(HorseBaseEntity.class)
public abstract class PredictableHorseBreeding extends AnimalEntity {

    protected PredictableHorseBreeding(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "setChildAttributes", at = @At(value = "HEAD"), cancellable = true)
    protected void setChildAttributes(PassiveEntity mate, HorseBaseEntity child, CallbackInfo ci)
    {
        child.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH
            ).setBaseValue(
                provideRandomStat(
                    this,
                    mate,
                    EntityAttributes.GENERIC_MAX_HEALTH,
                    15d,
                    30d
                )
        );

        child.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH
            ).setBaseValue(
                provideRandomStat(
                    this,
                    mate,
                    EntityAttributes.HORSE_JUMP_STRENGTH,
                    0.4,
                    1d
                )
        );
        
        child.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED
            ).setBaseValue(
                provideRandomStat(
                    this,
                    mate,
                    EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    0.1125,
                    0.3375
                )
        );

        ci.cancel();
    }

    private double provideRandomStat(
        LivingEntity parent1,
        LivingEntity parent2,
        EntityAttribute attribute,
        double statMin,
        double statMax
    )
    {
        // Cache their stats
        double p1Stat = parent1.getAttributeBaseValue(attribute);
        double p2Stat = parent2.getAttributeBaseValue(attribute);

        // Then check which is higher
        double higherStat = (p1Stat > p2Stat)? p1Stat : p2Stat;
        double lowerStat  = (p1Stat < p2Stat)? p1Stat : p2Stat;

        // Then give the higher stat a lot more weight
        double weightedAvg = (higherStat * 0.65) + (lowerStat * 0.35);

        // Set the minimum range to be the weighted average of the two parents minus 6%
        double min = weightedAvg * 0.94;

        // Then do the same for the max but give it a lower probability of taking the higher stat
        double max = weightedAvg * 1.04;

        // Find the range so we can make a random number
        double range = max - min;
        // Give it a random value
        double stat = min + (this.random.nextDouble() * range);

        SendMessages.sendMessageToAll(this.world,
            String.format(
                "Stats:\n" +
                "    Stat Name: %s\n" +
                "    Stat Max: %.5f\n" +
                "    Stat Min: %.5f\n" +
                "    Parent 1: %.5f\n" +
                "    Parent 2: %.5f\n" +
                "    Weighted Average: %.5f\n" +
                "Randomness:\n" +
                "    Min: %.5f\n" +
                "    Max: %.5f\n" +
                "    Range: %.5f\n" +
                "    Result: %.5f\n",
                attribute.getTranslationKey(),
                statMax,
                statMin,
                p1Stat,
                p2Stat,
                weightedAvg,
                min,
                max,
                range,
                (stat > statMax)? statMax : (stat < statMin)? statMin : stat
            )
        );
        
        // Then cap the value
        // Capping afterwards gives a much greater chance of getting to the maximum (or minimum) value
        // Because of this, the chance of getting a worse-than-the-weighted-average horse needs to be adjusted
        return (stat > statMax)? statMax : (stat < statMin)? statMin : stat;
    }
    
    /*

    From 1.17.1

    protected void setChildAttributes(PassiveEntity mate, HorseBaseEntity child)
    {
        double unDistHealth =
            this.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) +
            mate.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) +
            (double)this.getChildHealthBonus();
        
        child.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(unDistHealth / 3.0);

        double unDistJump =
            this.getAttributeBaseValue(EntityAttributes.HORSE_JUMP_STRENGTH) +
            mate.getAttributeBaseValue(EntityAttributes.HORSE_JUMP_STRENGTH) +
            this.getChildJumpStrengthBonus();
        
        child.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(unDistJump / 3.0);

        double unDistSpeed =
            this.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) +
            mate.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) +
            this.getChildMovementSpeedBonus();
        
        child.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(unDistSpeed / 3.0);
    }

    
    SE 16

    nextInt​(int bound)
        Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the specified value (exclusive), drawn from this random number generator's sequence.
    
    From 1.17.1
    
    protected float getChildHealthBonus() {
        // Between 15i - 30i

        return 15.0f + (float)this.random.nextInt(8) + (float)this.random.nextInt(9);
    }

    protected double getChildJumpStrengthBonus() {
        // Between 0.4 - 1
        // (i.e. up to 5.3 metres)

        return (double)0.4f +
            this.random.nextDouble() * 0.2 +
            this.random.nextDouble() * 0.2 +
            this.random.nextDouble() * 0.2;
    }

    protected double getChildMovementSpeedBonus() {
        // Between 0.1125 - 0.3375
        // (i.e. up to 14.228 m/s)

        return (
                // Between 0.45 - 1.35

                (double)0.45f +
                this.random.nextDouble() * 0.3 +
                this.random.nextDouble() * 0.3 +
                this.random.nextDouble() * 0.3

            ) * 0.25;
    }

    */
}
