package dev.renoux.ghost.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.renoux.ghost.Ghost;
import dev.renoux.ghost.utils.LivingEntityWithEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRendererDispatcher {
    @Inject(method = "renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V", at = @At("HEAD"), cancellable = true)
    private static void ghost$cancelShadow(PoseStack matrices, MultiBufferSource vertexConsumers, Entity entity, float opacity, float tickDelta, LevelReader world, float radius, CallbackInfo ci) {
        if (entity instanceof LivingEntity living && ((LivingEntityWithEffects) living).ghost$getGhostState() && !Minecraft.getInstance().player.is(entity)) {
            Matrix4f matrix = matrices.last().pose();
            Vector3f position = new Vector3f();
            Vector3f positionEyes = new Vector3f(0, living.getEyeHeight(), 0);
            position.mulPosition(matrix);
            positionEyes.mulPosition(matrix);

            double dist = living.position().distanceTo(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());

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
}
