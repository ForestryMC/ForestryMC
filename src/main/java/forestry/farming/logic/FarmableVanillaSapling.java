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
package forestry.farming.logic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.arboriculture.ITree;
import forestry.api.genetics.IIndividual;
import forestry.core.utils.GeneticsUtil;
import forestry.plugins.PluginArboriculture;

public class FarmableVanillaSapling extends FarmableGenericSapling {

	public FarmableVanillaSapling() {
		super(Blocks.sapling, -1, new ItemStack(Items.apple), new ItemStack(FarmableCocoa.COCOA_SEED, 1, FarmableCocoa.COCOA_META));
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		IIndividual tree = GeneticsUtil.getGeneticEquivalent(germling);
		if (!(tree instanceof ITree)) {
			return false;
		}

		return PluginArboriculture.treeInterface.plantSapling(world, (ITree) tree, player.getGameProfile(), pos);
	}

}
