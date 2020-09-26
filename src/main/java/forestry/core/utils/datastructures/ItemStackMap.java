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
package forestry.core.utils.datastructures;

import forestry.core.utils.ItemStackUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class ItemStackMap<T> extends StackMap<ItemStack, T> {
    private static final long serialVersionUID = -8511966739130702305L;

    @Override
    protected boolean areEqual(ItemStack a, Object b) {
        if (b instanceof ItemStack) {
            ItemStack b2 = (ItemStack) b;
            return ItemStackUtil.isCraftingEquivalent(b2, a);
        }

        if (b instanceof Item) {
            return a.getItem() == b;
        }

        if (b instanceof String) {
            return areEqual(a, new ResourceLocation((String) b));
        }

        if (b instanceof ResourceLocation) {
            ITagCollection<Item> collection = ItemTags.getCollection();
            ITag<Item> itemTag = collection.get((ResourceLocation) b);
            if (itemTag == null) {
                return false;
            }

            for (Item item : itemTag.getAllElements()) {
                if (areEqual(a, item)) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    @Override
    protected boolean isValidKey(Object key) {
        return key instanceof ItemStack || key instanceof Item || key instanceof String || key instanceof ResourceLocation;
    }

    @Override
    protected ItemStack getStack(Object key) {
        if (key instanceof ItemStack) {
            return (ItemStack) key;
        }
        return ItemStack.EMPTY;
    }
}
