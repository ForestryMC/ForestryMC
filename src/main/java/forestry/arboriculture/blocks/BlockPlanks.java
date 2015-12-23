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

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.TreeManager;

public class BlockPlanks extends BlockWood {

	public BlockPlanks(boolean fireproof) {
		super("planks", fireproof);
		setResistance(5.0F);
		setHarvestLevel("axe", 0);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List list) {
		for (EnumWoodType woodType : EnumWoodType.VALUES) {
			list.add(TreeManager.woodItemAccess.getPlanks(woodType, isFireproof()));
		}
	}

}
