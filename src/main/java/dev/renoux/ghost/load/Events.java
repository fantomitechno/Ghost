package dev.renoux.ghost.load;

import dev.renoux.ghost.Ghost;
import dev.renoux.ghost.networking.EffectPacket;
import dev.renoux.ghost.utils.LivingEntityWithEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.quiltmc.qsl.entity.event.api.ServerEntityLoadEvents;
import org.quiltmc.qsl.entity.event.api.client.ClientEntityLoadEvents;
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
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
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(packet.getEffectResourceLocation());
                if (entity instanceof LivingEntity livingEntity) {
                    if (effect.equals(ModRegistries.TRANSPARENCY_EFFECT)) {
                        ((LivingEntityWithEffects) livingEntity).ghost$setTransparency(packet.isActive());
                    } else if (effect.equals(ModRegistries.GHOST_STATE_EFFECT)) {
                        ((LivingEntityWithEffects) livingEntity).ghost$setGhostState(packet.isActive());
                    }
                }
            }
        });
    }

    private static void initClientNetworking() {
        ClientPlayNetworking.registerGlobalReceiver(EffectPacket.PACKET, (client, handler, buf, responseSender) -> {
            EffectPacket packet = new EffectPacket(buf);
            client.execute(() -> {
                Entity entity = client.level.getEntity(packet.getEntityId());
                if (entity != null) {
                    MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(packet.getEffectResourceLocation());
                    if (entity instanceof LivingEntity livingEntity) {
                        if (effect.equals(ModRegistries.TRANSPARENCY_EFFECT)) {
                            ((LivingEntityWithEffects) livingEntity).ghost$setTransparency(packet.isActive());
                        } else if (effect.equals(ModRegistries.GHOST_STATE_EFFECT)) {
                            ((LivingEntityWithEffects) livingEntity).ghost$setGhostState(packet.isActive());
                        }
                    }
                } else {
                    packetMap.put(packet.getEntityId(), packet);
                }
            });
        });
    }
}
