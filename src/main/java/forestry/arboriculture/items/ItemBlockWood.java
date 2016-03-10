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

import forestry.api.arboriculture.EnumWoodType;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.core.items.ItemBlockForestry;

public class ItemBlockWood<B extends Block & IWoodTyped> extends ItemBlockForestry<B> {
	public ItemBlockWood(Block block) {
		super(block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		IWoodTyped wood = getBlock();
		int meta = itemstack.getMetadata();
		EnumWoodType woodType = wood.getWoodType(meta);
		return WoodHelper.getDisplayName(wood, woodType);
	}

}
