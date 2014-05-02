/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
import forestry.core.config.Config;
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

		return PluginArboriculture.treeInterface.plantSapling(world, tree, Config.fakeUserLogin, x, y, z);
	}

}
