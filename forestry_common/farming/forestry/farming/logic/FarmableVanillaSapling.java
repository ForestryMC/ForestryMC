/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.farming.logic;

import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.arboriculture.ITree;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.core.utils.Utils;
import forestry.plugins.PluginArboriculture;

public class FarmableVanillaSapling extends FarmableGenericSapling {

	int saplingId;
	int saplingMeta;

	public FarmableVanillaSapling() {
		super(Blocks.sapling, -1, new ItemStack(Items.apple), new ItemStack(FarmableCocoa.COCOA_SEED, 1, FarmableCocoa.COCOA_META));
	}

	@Override
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {
		ITree tree = null;
		for (Map.Entry<ItemStack, IIndividual> entry : AlleleManager.ersatzSaplings.entrySet())
			if (entry.getKey().isItemEqual(germling) && entry.getValue() instanceof ITree) {
				tree = (ITree) entry.getValue();
				break;
			}

		if (tree == null)
			return false;

		return PluginArboriculture.treeInterface.plantSapling(world, tree, Utils.getForestryPlayerProfile(), x, y, z);
	}

}
