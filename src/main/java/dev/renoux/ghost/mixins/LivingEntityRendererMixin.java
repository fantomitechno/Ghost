package dev.renoux.ghost.mixins;


import com.mojang.blaze3d.vertex.PoseStack;
import dev.renoux.ghost.Ghost;
import dev.renoux.ghost.utils.LivingEntityWithEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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
        if (((LivingEntityWithEffects) livingEntity).ghost$getGhostState() && !Minecraft.getInstance().player.is(livingEntity)) {
            Matrix4f matrix = poseStack.last().pose();
            Vector3f position = new Vector3f();
            Vector3f positionEyes = new Vector3f(0, livingEntity.getEyeHeight(), 0);
            position.mulPosition(matrix);
            positionEyes.mulPosition(matrix);

            double dist = livingEntity.position().distanceTo(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());


            int h = Minecraft.getInstance().gui.screenHeight;
            int w = Minecraft.getInstance().gui.screenWidth;

            double x = (position.x / dist) * ((double) w / 2) + (double) w / 2;
            double yDown = (positionEyes.y / dist) * ((double) h / 2) + (double) h / 2;
            double yUp = (position.y / dist) * ((double) h / 2) + (double) h / 2;

            double borderX = w * Ghost.BORDER;
            double borderY = h * Ghost.BORDER*2;

            if (x > borderX && x < w - borderX && yDown > borderY && yUp < h - borderY)
                ci.cancel();
        }
    }

    @Inject(method = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;isBodyVisible(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"), cancellable = true)
    private void ghost$transparencyEffect(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (((LivingEntityWithEffects) entity).ghost$getTransparency()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
