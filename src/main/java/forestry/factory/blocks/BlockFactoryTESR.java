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
package forestry.factory.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

import forestry.core.blocks.BlockBase;

public class BlockFactoryTesr extends BlockBase {
	public BlockFactoryTesr() {
		super(Material.iron, true);
	}

	public ItemStack get(BlockFactoryTesrType type) {
		return new ItemStack(this, 1, type.ordinal());
	}
}
