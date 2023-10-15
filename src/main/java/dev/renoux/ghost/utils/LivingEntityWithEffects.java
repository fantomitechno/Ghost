package dev.renoux.ghost.utils;

public interface LivingEntityWithEffects {
    void ghost$setGhostState(boolean ghostState);

    void ghost$setTransparency(boolean transparency);

    boolean ghost$getGhostState();

    boolean ghost$getTransparency();
}
