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
package forestry.core.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import forestry.core.blocks.IItemTyped;

/**
 * For blocks whose type depends on metadata.
 * This allows control over which localized name maps to which meta value.
 */
public class ItemBlockTyped extends ItemBlockForestry {

	public ItemBlockTyped(Block block) {
		super(block);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public String getUnlocalizedName(ItemStack itemstack) {
		Block block = getBlock();
		if (block instanceof IItemTyped) {
			IItemTyped blockTyped = (IItemTyped) block;
			Enum type = blockTyped.getTypeFromMeta(itemstack.getItemDamage());
			if (type != null) {
				return getBlock().getUnlocalizedName() + "." + type.ordinal();
			} else {
				return null;
			}
		}
		return super.getUnlocalizedName(itemstack);
	}
}
