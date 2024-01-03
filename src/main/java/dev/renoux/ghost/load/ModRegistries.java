/*
 * MIT License
 *
 * Copyright (c) 2024 Simon RENOUX aka fantomitechno
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
package dev.renoux.ghost.load;

import dev.renoux.ghost.effects.GhostStateEffect;
import dev.renoux.ghost.effects.TransparencyEffect;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.CreativeModeTabs;
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
    public static Item GHOST_ESSENCE;

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
        GHOST_TOTEM = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(metadata.id(), "ghost_totem"), new Item(new QuiltItemSettings().stacksTo(1)));
        GHOST_ESSENCE = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(metadata.id(), "ghost_essence"), new Item(new QuiltItemSettings().stacksTo(16).fireResistant()));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((entries -> entries.addAfter(Items.RECOVERY_COMPASS, GHOST_TOTEM, GHOST_ESSENCE)));
    }

    public static void initPotions() {
        TRANSPARENCY_POTION = Registry.register(BuiltInRegistries.POTION, new ResourceLocation(metadata.id(), "transparency"), new Potion(new MobEffectInstance(TRANSPARENCY_EFFECT, 3600)));
        LONG_TRANSPARENCY_POTION = Registry.register(BuiltInRegistries.POTION, new ResourceLocation(metadata.id(), "long_transparency"), new Potion(new MobEffectInstance(TRANSPARENCY_EFFECT, 9600)));

        FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.INVISIBILITY, Ingredient.of(Items.GOLDEN_APPLE), TRANSPARENCY_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.LONG_INVISIBILITY, Ingredient.of(Items.GOLDEN_APPLE), LONG_TRANSPARENCY_POTION);
    }
}
