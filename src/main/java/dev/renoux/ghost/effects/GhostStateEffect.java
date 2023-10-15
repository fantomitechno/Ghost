package dev.renoux.ghost.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class GhostStateEffect extends MobEffect {
    public GhostStateEffect() {
        super(
                MobEffectCategory.NEUTRAL,
                0xE0E0E0);
    }
}
