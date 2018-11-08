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
package forestry.arboriculture.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.IWoodType;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.core.items.ItemBlockForestry;

public class ItemBlockWood<B extends Block & IWoodTyped> extends ItemBlockForestry<B> {
	public ItemBlockWood(B block) {
		super(block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		IWoodTyped wood = getBlock();
		int meta = itemstack.getMetadata();
		IWoodType woodType = wood.getWoodType(meta);
		return WoodHelper.getDisplayName(wood, woodType);
	}

	@Override
	public int getItemBurnTime(ItemStack itemStack) {
		B block = getBlock();
		if (block.isFireproof()) {
			return 0;
		} else {
			return 300;
		}
	}
}
