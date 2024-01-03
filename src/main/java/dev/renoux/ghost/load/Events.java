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
package dev.renoux.ghost.load;

import dev.renoux.ghost.Ghost;
import dev.renoux.ghost.networking.EffectPacket;
import dev.renoux.ghost.utils.LivingEntityWithEffects;
import dev.renoux.ghost.utils.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.quiltmc.qsl.entity.event.api.client.ClientEntityLoadEvents;
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.HashMap;
import java.util.Map;

public class Events {

    public static Map<Number, EffectPacket> packetMap = new HashMap<>();

    public static void register() {
        initCommands();
        initEvents();
        initClientNetworking();
    }

    private static void initCommands() {
    }

    private static void initEvents() {
        ServerTickEvents.START.register(server -> {
            Ghost.server = server;
        });

        ClientEntityLoadEvents.AFTER_LOAD.register((entity, world) -> {
            if (packetMap.containsKey(entity.getId())) {
                EffectPacket packet = packetMap.get(entity.getId());
                Utils.handleEffectPacket(packet, entity);
            }
        });
    }

    private static void initClientNetworking() {
        ClientPlayNetworking.registerGlobalReceiver(EffectPacket.PACKET, (client, handler, buf, responseSender) -> {
            EffectPacket packet = new EffectPacket(buf);
            client.execute(() -> {
                Entity entity = client.level.getEntity(packet.getEntityId());
                if (entity != null) {
                    Utils.handleEffectPacket(packet, entity);
                } else {
                    packetMap.put(packet.getEntityId(), packet);
                }
            });
        });
    }
}
