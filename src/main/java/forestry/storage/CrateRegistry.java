/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.storage;

import forestry.api.core.IItemProvider;
import forestry.api.storage.ICrateRegistry;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.storage.items.ItemCrated;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;

import java.util.Collection;

public class CrateRegistry implements ICrateRegistry {

    private static void setRegisterCrate(ItemStack stack) {
        if (stack.isEmpty()) {
            Log.error("Tried to make a crate without an item");
            return;
        }

        String stringForItemStack = ItemStackUtil.getStringForItemStack(stack);
        if (stringForItemStack == null) {
            Log.error("Could not get string name for itemStack {}", stack);
            return;
        }
        String itemName = stringForItemStack.replace(':', '.');
        String crateName = "crated." + itemName;

        IFeatureRegistry registry = ModFeatureRegistry.get(ModuleCrates.class);
        ModuleCrates.registerCrate(registry.item(() -> new ItemCrated(stack), crateName));
    }

    @Override
    public void registerCrate(ITag tag) {
        if (ModuleCrates.cratesRejectedOreDict.contains(tag.toString())) {
            return;
        }

        Ingredient ingredient = Ingredient.fromTag(tag);
        for (ItemStack stack : ingredient.getMatchingStacks()) {
            if (stack != null) {
                registerCrate(stack);
                break;
            }
        }
    }

    @Override
    public void registerCrate(Block block) {
        setRegisterCrate(new ItemStack(block));
    }

    @Override
    public void registerCrate(Item item) {
        setRegisterCrate(new ItemStack(item));
    }

    @Override
    public void registerCrate(IItemProvider provider) {
        if (provider.hasItem()) {
            registerCrate(provider.item());
        }
    }

    @Override
    public void registerCrate(ItemStack stack) {
        Collection<ItemStack> testStacks = ModuleCrates.cratesRejectedItem.get(stack.getItem());
        for (ItemStack testStack : testStacks) {
            if (ItemStackUtil.areItemStacksEqualIgnoreCount(stack, testStack)) {
                return;
            }
        }

        setRegisterCrate(stack);
    }

    @Override
    public void blacklistCrate(ItemStack stack) {
        ModuleCrates.cratesRejectedItem.put(stack.getItem(), stack);
    }
}
