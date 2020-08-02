package forestry.core.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import forestry.modules.features.FeatureItem;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;

public class JeiUtil {
    public static final String DESCRIPTION_KEY = "for.jei.description.";

    private JeiUtil() {
    }

    public static void addDescription(IRecipeRegistration registry, String itemKey, FeatureItem... items) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (FeatureItem item : items) {
            itemStacks.add(item.stack());
        }

        registry.addIngredientInfo(itemStacks, VanillaTypes.ITEM, DESCRIPTION_KEY + itemKey);
    }

    public static void addDescription(IRecipeRegistration registry, String itemKey, Item... items) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (Item item : items) {
            itemStacks.add(new ItemStack(item));
        }

        registry.addIngredientInfo(itemStacks, VanillaTypes.ITEM, DESCRIPTION_KEY + itemKey);
    }

    public static void addDescription(IRecipeRegistration registry, Block... blocks) {
        for (Block block : blocks) {
            Item item = Item.getItemFromBlock(block);
            if (item != Items.AIR) {
                addDescription(registry, item);
            } else {
                Log.error("No item for block {}", block);
            }
        }
    }

    public static void addDescription(IRecipeRegistration registry, FeatureItem... items) {
        for (FeatureItem item : items) {
            addDescription(registry, item.get());
        }
    }

    public static void addDescription(IRecipeRegistration registry, Item... items) {
        for (Item item : items) {
            addDescription(registry, item);
        }
    }

    public static void addDescription(IRecipeRegistration registry, Item item) {
        String resourcePath = item.getRegistryName().getPath();
        addDescription(registry, item, resourcePath);
    }

    public static void addDescription(IRecipeRegistration registry, Item item, String itemKey) {
        ItemStack itemStack = new ItemStack(item);
        registry.addIngredientInfo(itemStack, VanillaTypes.ITEM, DESCRIPTION_KEY + itemKey);
    }
}
