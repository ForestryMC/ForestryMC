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
package forestry.apiculture.genetics;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.core.utils.StackUtils;

public class MutationReqRes extends BeeMutation {

	private final ItemStack blockRequired;

	public MutationReqRes(IAllele allele0, IAllele allele1, IAllele[] template, int chance, ItemStack blockRequired) {
		super(allele0, allele1, template, chance);
		this.blockRequired = blockRequired;
	}

	@Override
	public float getChance(IBeeHousing housing, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		float chance = super.getChance(housing, allele0, allele1, genome0, genome1);

		// If we have no chance anyway, we don't need to check.
		if (chance <= 0)
			return 0;

		World world = housing.getWorld();
		if (blockRequired == null)
			return chance;

		Block block = world.getBlock(housing.getXCoord(), housing.getYCoord() - 1, housing.getZCoord());
		int meta = world.getBlockMetadata(housing.getXCoord(), housing.getYCoord() - 1, housing.getZCoord());
		if (StackUtils.equals(block, blockRequired) && meta == blockRequired.getItemDamage())
			return chance;
		else
			return 0;
	}
}
