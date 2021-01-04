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
package forestry.arboriculture.items;

import forestry.api.arboriculture.IWoodType;
import forestry.api.core.ItemGroups;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.core.items.ItemBlockForestry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class ItemBlockWood<B extends Block & IWoodTyped> extends ItemBlockForestry<B> {
    public ItemBlockWood(B block) {
        super(block, new Item.Properties().group(ItemGroups.tabArboriculture));
    }

    @Override
    public ITextComponent getDisplayName(ItemStack itemstack) {
        IWoodTyped wood = getBlock();
        IWoodType woodType = wood.getWoodType();
        return WoodHelper.getDisplayName(wood, woodType);
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        B block = getBlock();
        if (block.isFireproof()) {
            return 0;
        } else {
            return 300;
        }
    }
}
