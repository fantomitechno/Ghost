package dev.renoux.ghost.networking;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;

import static dev.renoux.ghost.Ghost.metadata;

public class EffectPacket implements Packet<ClientGamePacketListener> {
    public static final ResourceLocation PACKET = new ResourceLocation(metadata.id(), "effect");

    private final int entityId;

    private final ResourceLocation effectResourceLocation;

    private final boolean isActive;

    public EffectPacket(Entity entity, MobEffect effect, boolean isActive) {
        this.entityId = entity.getId();
        this.effectResourceLocation = BuiltInRegistries.MOB_EFFECT.getKey(effect);
        this.isActive = isActive;
    }

    public EffectPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.effectResourceLocation = buf.readResourceLocation();
        this.isActive = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeResourceLocation(this.effectResourceLocation);
        buf.writeBoolean(this.isActive);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
    }

    public ResourceLocation getEffectResourceLocation() {
        return this.effectResourceLocation;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public boolean isActive() {
        return isActive;
    }
}
