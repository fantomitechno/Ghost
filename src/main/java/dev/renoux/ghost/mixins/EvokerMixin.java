package dev.renoux.ghost.mixins;

import dev.renoux.ghost.load.ModRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Evoker.class)
public abstract class EvokerMixin extends SpellcasterIllager {
    protected EvokerMixin(EntityType<? extends SpellcasterIllager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        if (this.getEffect(ModRegistries.TRANSPARENCY_EFFECT) != null && Math.random() < 0.1 + (double) lootingMultiplier / 20) {
            ItemEntity itemEntity = this.spawnAtLocation(ModRegistries.GHOST_TOTEM);
            if (itemEntity != null) {
                itemEntity.setExtendedLifetime();
            }
        }
    }
}
