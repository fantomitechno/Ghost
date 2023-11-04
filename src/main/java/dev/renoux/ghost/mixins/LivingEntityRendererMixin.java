package dev.renoux.ghost.mixins;


import com.mojang.blaze3d.vertex.PoseStack;
import dev.renoux.ghost.utils.LivingEntityWithEffects;
import dev.renoux.ghost.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    protected LivingEntityRendererMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
    private void ghost$getPosOnScreen(T livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (((LivingEntityWithEffects) livingEntity).ghost$getGhostState()
                && !Minecraft.getInstance().player.is(livingEntity)
                && Utils.calculateGhostEffect(poseStack, livingEntity))
            ci.cancel();
    }

    @Inject(method = "isBodyVisible(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"), cancellable = true)
    private void ghost$transparencyEffect(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (((LivingEntityWithEffects) entity).ghost$getTransparency() || ((LivingEntityWithEffects) entity).ghost$getGhostState() && Minecraft.getInstance().player.is(entity)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
