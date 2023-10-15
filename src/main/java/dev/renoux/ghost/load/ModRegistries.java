package dev.renoux.ghost.load;

import dev.renoux.ghost.effects.GhostStateEffect;
import dev.renoux.ghost.effects.TransparencyEffect;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import static dev.renoux.ghost.Ghost.metadata;

public class ModRegistries {
    public static GhostStateEffect GHOST_STATE_EFFECT = new GhostStateEffect();
    public static TransparencyEffect TRANSPARENCY_EFFECT = new TransparencyEffect();

    public static Item GHOST_TOTEM;

    public static Potion TRANSPARENCY_POTION;
    public static Potion LONG_TRANSPARENCY_POTION;

    public static void init() {

        initMobEffects();
        initItems();
        initPotions();
    }

    public static void initMobEffects() {
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(metadata.id(), "ghost_state"), GHOST_STATE_EFFECT);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(metadata.id(), "transparency"), TRANSPARENCY_EFFECT);
    }

    public static void initItems() {
          GHOST_TOTEM = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(metadata.id(), "ghost_totem"), new Item(new QuiltItemSettings().maxCount(1)));
    }

    public static void initPotions() {
        TRANSPARENCY_POTION = Registry.register(BuiltInRegistries.POTION, new ResourceLocation(metadata.id(), "transparency"), new Potion(new MobEffectInstance(TRANSPARENCY_EFFECT, 3600)));
        LONG_TRANSPARENCY_POTION = Registry.register(BuiltInRegistries.POTION, new ResourceLocation(metadata.id(), "transparency"), new Potion(new MobEffectInstance(TRANSPARENCY_EFFECT, 9600)));

        FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.INVISIBILITY, Ingredient.of(Items.GOLDEN_APPLE), TRANSPARENCY_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.LONG_INVISIBILITY, Ingredient.of(Items.GOLDEN_APPLE), LONG_TRANSPARENCY_POTION);
    }
}
