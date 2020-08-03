/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FallbackIngredient extends Ingredient {
    private final Ingredient primary;
    private final Ingredient fallback;

    public static Ingredient fromItems(IItemProvider primary, IItemProvider fallback) {
        return fromIngredients(Ingredient.fromItems(primary), Ingredient.fromItems(fallback));
    }

    public static Ingredient fromStacks(ItemStack primary, ItemStack fallback) {
        return fromIngredients(Ingredient.fromStacks(primary), Ingredient.fromStacks(fallback));
    }

    public static Ingredient fromTag(ITag<Item> primary, ItemStack fallback) {
        return fromIngredients(Ingredient.fromTag(primary), Ingredient.fromStacks(fallback));
    }

    public static Ingredient fromTag(ITag<Item> primary, ITag<Item> fallback) {
        return fromIngredients(Ingredient.fromTag(primary), Ingredient.fromTag(fallback));
    }

    public static Ingredient fromIngredients(Ingredient primary, Ingredient fallback) {
        return new FallbackIngredient(primary, fallback);
    }

    private FallbackIngredient(Ingredient primary, Ingredient fallback) {
        super(Stream.of());
        this.primary = primary;
        this.fallback = fallback;
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.add("primary", primary.serialize());
        jsonobject.add("fallback", fallback.serialize());
        return jsonobject;
    }

    public static class Serializer implements IIngredientSerializer<Ingredient> {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {
        }

        @Override
        public Ingredient parse(PacketBuffer buffer) {
            return Ingredient.read(buffer);
        }

        @Override
        public void write(PacketBuffer buffer, Ingredient ingredient) {
            ingredient.write(buffer);
        }

        @Nonnull
        @Override
        public Ingredient parse(JsonObject json) {
            Ingredient ret;
            try {
                JsonArray arr = JSONUtils.getJsonArray(json, "primary");
                List<Ingredient> ingredientList = new ArrayList<>();
                for (JsonElement element : arr) {
                    if (!(element instanceof JsonObject)) {
                        throw new JsonSyntaxException("Didn't supply json object for ingredient!");
                    }
                    JsonObject obj = (JsonObject) element;
                    ingredientList.add(CraftingHelper.getIngredient(obj));
                }
                ret = Ingredient.merge(ingredientList);
            } catch (JsonSyntaxException e) {
                ret = Ingredient.EMPTY;    //throws exception if item doesn't exist
            }
            if (ret.getMatchingStacks().length == 0) {
                JsonArray fallbackArr = JSONUtils.getJsonArray(json, "fallback");
                List<Ingredient> ingredients = new ArrayList<>();
                for (JsonElement element : fallbackArr) {
                    if (!(element instanceof JsonObject)) {
                        throw new JsonSyntaxException("Didn't supply json object for ingredient!");
                    }
                    JsonObject obj = (JsonObject) element;
                    ingredients.add(CraftingHelper.getIngredient(obj));
                }
                ret = Ingredient.merge(ingredients);
            }
            return ret;
        }
    }
}
