package dev.renoux.ghost.mixins;

import dev.renoux.ghost.load.ModRegistries;
import dev.renoux.ghost.networking.EffectPacket;
import dev.renoux.ghost.utils.LivingEntityWithEffects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityWithEffects {

    @Shadow public abstract @Nullable MobEffectInstance getEffect(MobEffect effect);

    @Shadow public abstract ItemStack getItemInHand(InteractionHand hand);

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract boolean removeAllEffects();

    @Shadow public abstract boolean addEffect(MobEffectInstance effect);

    @Unique
    private boolean ghostState = false;

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
            } else {
                ghost$setGhostState(false);
            };
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
        if (this.getEffect(ModRegistries.TRANSPARENCY_EFFECT) != null && Math.random() < 0.3 + (double) lootingMultiplier / 10) {
            ItemEntity itemEntity = this.spawnAtLocation(ModRegistries.GHOST_ESSENCE);
            if (itemEntity != null) {
                itemEntity.setExtendedLifetime();
            }
        }
    }

    @Inject(method = "checkTotemDeathProtection(Lnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("HEAD"), cancellable = true)
    private void ghost$checkTotemDeathProtection(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            ItemStack itemStack = null;

            for(InteractionHand interactionHand : InteractionHand.values()) {
                ItemStack itemStack2 = this.getItemInHand(interactionHand);
                if (itemStack2.is(ModRegistries.GHOST_TOTEM)) {
                    itemStack = itemStack2.copy();
                    itemStack2.shrink(1);
                    break;
                }
            }

            if (itemStack != null) {
                ServerPlayer serverPlayer = this.getServer().getPlayerList().getPlayer(this.getUUID());
                if (serverPlayer != null) {
                    serverPlayer.awardStat(Stats.ITEM_USED.get(ModRegistries.GHOST_TOTEM));
                    CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, itemStack);
                }

                this.setHealth(1.0F);
                this.removeAllEffects();
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1));
                this.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1));
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0));
                this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0));
                this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3));
                this.addEffect(new MobEffectInstance(ModRegistries.GHOST_STATE_EFFECT, -1, 0));
                this.level().broadcastEntityEvent(this, (byte)22);
                cir.setReturnValue(true);
                cir.cancel();
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
