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
package forestry.arboriculture.worldgen;

import net.minecraft.block.BlockStairs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class BlockTypeWoodStairs extends BlockTypeWood {
	public BlockTypeWoodStairs(ItemStack itemStack) {
		super(itemStack);
	}

	@Override
	public void setDirection(EnumFacing facing) {
		state = state.withProperty(BlockStairs.FACING, facing);
	}
}
