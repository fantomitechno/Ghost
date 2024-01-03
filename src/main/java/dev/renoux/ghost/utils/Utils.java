/*
 * MIT License
 *
 * Copyright (c) 2024 Simon RENOUX aka fantomitechno
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.renoux.ghost.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.renoux.ghost.Ghost;
import dev.renoux.ghost.load.ModRegistries;
import dev.renoux.ghost.networking.EffectPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
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

    public static void handleEffectPacket(EffectPacket packet, Entity entity) {
        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(packet.getEffectResourceLocation());
        if (entity instanceof LivingEntity livingEntity) {
            if (effect.equals(ModRegistries.TRANSPARENCY_EFFECT)) {
                ((LivingEntityWithEffects) livingEntity).ghost$setTransparency(packet.isActive());
            } else if (effect.equals(ModRegistries.GHOST_STATE_EFFECT)) {
                ((LivingEntityWithEffects) livingEntity).ghost$setGhostState(packet.isActive());
            }
        }
    }
}
