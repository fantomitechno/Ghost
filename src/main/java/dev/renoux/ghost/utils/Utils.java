package dev.renoux.ghost.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.renoux.ghost.Ghost;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Utils {
    public static boolean calculateGhostEffect(PoseStack poseStack, LivingEntity livingEntity) {
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

        return x > borderX && x < w - borderX && yDown > borderY && yUp < h - borderY;
    }
}
