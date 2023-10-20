package dev.renoux.ghost.mixins;

import dev.renoux.ghost.load.ModRegistries;
import dev.renoux.ghost.networking.EffectPacket;
import dev.renoux.ghost.utils.LivingEntityWithEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "sendPairingData(Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V", at = @At("TAIL"))
    private void pairEffects(ServerPlayer player, Consumer<Packet<ClientGamePacketListener>> sender, CallbackInfo ci) {
        if (this.entity instanceof LivingEntity livingEntity) {
            for (MobEffectInstance effect : livingEntity.getActiveEffects()) {
                if (effect.getEffect().equals(ModRegistries.TRANSPARENCY_EFFECT) || effect.getEffect().equals(ModRegistries.GHOST_STATE_EFFECT)) {
                    LivingEntityWithEffects entityWithEffects = (LivingEntityWithEffects) livingEntity;
                    if (effect.getEffect().equals(ModRegistries.TRANSPARENCY_EFFECT)) {
                        entityWithEffects.ghost$setTransparency(true);
                    } else {
                        entityWithEffects.ghost$setGhostState(true);
                    };
                    EffectPacket packet = new EffectPacket(livingEntity, effect.getEffect(), true);
                    FriendlyByteBuf buf = PacketByteBufs.create();
                    packet.write(buf);
                    ServerPlayNetworking.send(player, EffectPacket.PACKET, buf);
                }
            }
        }
    }
}
