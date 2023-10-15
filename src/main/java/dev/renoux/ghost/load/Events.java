package dev.renoux.ghost.load;

import dev.renoux.ghost.Ghost;
import dev.renoux.ghost.networking.EffectPacket;
import dev.renoux.ghost.networking.NetworkingConstant;
import dev.renoux.ghost.utils.LivingEntityWithEffects;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.rmi.registry.Registry;

import static net.minecraft.commands.Commands.literal;

public class Events {
    public static void register() {
        initCommands();
        initServerTicking();
        initClientNetworking();
    }

    private static void initCommands() {
    }

    private static void initServerTicking() {
        ServerTickEvents.START.register(server -> {
            Ghost.server = server;
        });
    }

    private static void initClientNetworking() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstant.EFFECT, (client, handler, buf, responseSender) -> {
            EffectPacket packet = new EffectPacket(buf);
            client.execute(() -> {
                Entity entity = client.level.getEntity(packet.getEntityId());
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(packet.getEffectResourceLocation());
                if (entity instanceof LivingEntity livingEntity) {
                    if (effect.equals(ModRegistries.TRANSPARENCY_EFFECT)) {
                        ((LivingEntityWithEffects) livingEntity).ghost$setTransparency(packet.isActive());
                    } else if (effect.equals(ModRegistries.GHOST_STATE_EFFECT)) {
                        ((LivingEntityWithEffects) livingEntity).ghost$setGhostState(packet.isActive());
                    }
                }
            });
        });
    }
}
