/*
 * MIT License
 *
 * Copyright (c) 2023 Simon RENOUX aka fantomitechno
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
