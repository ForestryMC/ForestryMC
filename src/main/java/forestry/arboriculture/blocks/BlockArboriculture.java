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
package forestry.arboriculture.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

import forestry.api.core.Tabs;
import forestry.core.blocks.BlockBase;

public class BlockArboriculture extends BlockBase {
	public enum Type {
		ARBCHEST;
	}

	public BlockArboriculture() {
		super(Material.iron, true);
		setCreativeTab(Tabs.tabArboriculture);
	}

	public ItemStack get(Type type, int amount) {
		return new ItemStack(this, amount, type.ordinal());
	}
}
