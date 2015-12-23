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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import forestry.core.utils.StringUtil;

public class MutationConditionRequiresResourceOreDict implements IMutationCondition {

	private final int oreDictId;
	private final String displayName;

	public MutationConditionRequiresResourceOreDict(String oreDictName) {
		this.oreDictId = OreDictionary.getOreID(oreDictName);

		List<ItemStack> ores = OreDictionary.getOres(oreDictName);
		if (ores != null && 0 < ores.size()) {
			this.displayName = ores.get(0).getDisplayName();
		} else {
			this.displayName = oreDictName;
		}
	}

	@Override
	public float getChance(World world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		Block block;
		int meta;
		int i = 1;
		do {
			IBlockState state = world.getBlockState(pos.add(0, -i, 0));
			block = state.getBlock();
			meta = block.getMetaFromState(state);
			i++;
		} while (block instanceof IBeeHousing);

		int[] oreIds = OreDictionary.getOreIDs(new ItemStack(block, 1, meta));
		for (int oreId : oreIds) {
			if (oreId == this.oreDictId) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public String getDescription() {
		return StringUtil.localizeAndFormat("mutation.condition.resource", displayName);
	}
}
