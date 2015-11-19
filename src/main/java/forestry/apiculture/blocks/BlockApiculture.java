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
package forestry.apiculture.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

import forestry.api.core.Tabs;
import forestry.core.blocks.BlockBase;

public class BlockApiculture extends BlockBase {
	public enum Type {
		APIARY,
		APIARIST_CHEST_LEGACY,
		BEEHOUSE;
	}

	public BlockApiculture(Material material) {
		super(material);
		setCreativeTab(Tabs.tabApiculture);
		setHarvestLevel("axe", 0);
	}

	public ItemStack get(Type type, int amount) {
		return new ItemStack(this, amount, type.ordinal());
	}
}
