package dev.renoux.ghost.mixins;

import dev.renoux.ghost.utils.LivingEntityWithEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TargetingConditions.class)
public class TargetingConditionsMixin {

    @Shadow private double range;

    @Inject(method = "test(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void ghost$invisibleAsGhost(LivingEntity baseEntity, LivingEntity targetEntity, CallbackInfoReturnable<Boolean> cir) {
        if (((LivingEntityWithEffects) targetEntity).ghost$getGhostState()) {
            cir.setReturnValue(false);
            cir.cancel();
        } else if (((LivingEntityWithEffects) targetEntity).ghost$getTransparency()) {
            if (this.range > 0.0) {
                double e = 1.2;
                double f = baseEntity.distanceTo(targetEntity);
                if (f > e) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }
}
