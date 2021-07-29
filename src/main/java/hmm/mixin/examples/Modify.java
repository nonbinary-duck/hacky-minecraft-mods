package hmm.mixin.examples;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.LivingEntity;


@Mixin(LivingEntity.class)
public abstract class Modify {

    @ModifyConstant(method = "fall", constant = @Constant(doubleValue = 150.0))
    private static double fallParticlesChangeDensity(double original) {
		System.out.println("Density " + original);

        return 100.0 * original;
	}

    /// This doesn't work
    @ModifyArg(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"), index = 8)
    private double fallParticlesChangeSpeed(double original) {
		return 5 * original;
	}

    /// But these does, who knows why...
    @ModifyArg(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"), index = 5)
    private double fallParticleSizeX(double original) {
        return 0.05;
    }
    
    @ModifyArg(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"), index = 6)
    private double fallParticleSizeY(double original) {
        return 0.05;
    }
    
    @ModifyArg(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"), index = 7)
    private double fallParticleSizeZ(double original) {
        return 0.05;
    }
}
