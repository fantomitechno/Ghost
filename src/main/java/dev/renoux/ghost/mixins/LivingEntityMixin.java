package dev.renoux.ghost.mixins;

import dev.renoux.ghost.load.ModRegistries;
import dev.renoux.ghost.networking.EffectPacket;
import dev.renoux.ghost.utils.LivingEntityWithEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityWithEffects {

    @Shadow public abstract @Nullable MobEffectInstance getEffect(MobEffect effect);

    @Unique
    private boolean ghostState = false;

    @Unique
    private boolean permanentGhostState = false;

    @Unique
    private boolean transparency = false;

    public LivingEntityMixin(EntityType<?> variant, Level world) {
        super(variant, world);
    }

    @Inject(method = "onEffectAdded(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void ghost$captureAddEffects(MobEffectInstance effect, Entity source, CallbackInfo ci) {
        if (effect.getEffect().equals(ModRegistries.TRANSPARENCY_EFFECT) || effect.getEffect().equals(ModRegistries.GHOST_STATE_EFFECT)) {
            if (effect.getEffect().equals(ModRegistries.TRANSPARENCY_EFFECT)) {
                ghost$setTransparency(true);
            } else {
                ghost$setGhostState(true);
            };
            if (!this.level().isClientSide()) {
                EffectPacket packet = new EffectPacket(this, effect.getEffect(), true);
                FriendlyByteBuf buf = PacketByteBufs.create();
                packet.write(buf);
                for (ServerPlayer player : this.getServer().getPlayerList().getPlayers()) {
                    ServerPlayNetworking.send(player, EffectPacket.PACKET, buf);
                }
            }
        }
    }

    @Inject(method = "onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("HEAD"))
    private void ghost$captureRemoveEffects(MobEffectInstance effect, CallbackInfo ci) {
        if (effect.getEffect().equals(ModRegistries.TRANSPARENCY_EFFECT) || effect.getEffect().equals(ModRegistries.GHOST_STATE_EFFECT)) {
            if (effect.getEffect().equals(ModRegistries.TRANSPARENCY_EFFECT)) {
                ghost$setTransparency(false);
            }
            if (!this.level().isClientSide()) {
                EffectPacket packet = new EffectPacket(this, effect.getEffect(), false);
                FriendlyByteBuf buf = PacketByteBufs.create();
                packet.write(buf);
                for (ServerPlayer player : this.getServer().getPlayerList().getPlayers()) {
                    ServerPlayNetworking.send(player, EffectPacket.PACKET, buf);
                }
            }
        }
    }

    @Inject(method = "dropCustomDeathLoot(Lnet/minecraft/world/damagesource/DamageSource;IZ)V", at = @At("HEAD"))
    private void ghost$dropCustomDeathLoot(DamageSource source, int lootingMultiplier, boolean allowDrops, CallbackInfo ci) {
        if (this.getEffect(ModRegistries.TRANSPARENCY_EFFECT) != null && Math.random() > 0.3) {
            ItemEntity itemEntity = this.spawnAtLocation(ModRegistries.GHOST_TOTEM_SHARD);
            if (itemEntity != null) {
                itemEntity.setExtendedLifetime();
            }
        }
    }

    @Override
    public void ghost$setGhostState(boolean ghostState) {
        this.ghostState = ghostState;
    }

    @Override
    public void ghost$setTransparency(boolean transparency) {
        this.transparency = transparency;
    }

    @Override
    public boolean ghost$getGhostState() {
        return this.ghostState;
    }

    @Override
    public boolean ghost$getTransparency() {
        return this.transparency;
    }
}
