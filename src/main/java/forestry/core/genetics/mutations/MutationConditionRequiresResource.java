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
package forestry.core.genetics.mutations;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.StringUtil;

public class MutationConditionRequiresResource implements IMutationCondition {

	private final ItemStack blockRequired;

	public MutationConditionRequiresResource(Block block, int meta) {
		blockRequired = new ItemStack(block, meta);
	}

	@Override
	public float getChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		Block block = world.getBlock(x, y - 1, z);
		int meta = world.getBlockMetadata(x, y - 1, z);
		return ItemStackUtil.equals(block, meta, blockRequired) ? 1 : 0;
	}

	@Override
	public String getDescription() {
		return StringUtil.localizeAndFormat("mutation.condition.resource", blockRequired.getDisplayName());
	}
}
