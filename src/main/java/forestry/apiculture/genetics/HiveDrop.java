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

import java.util.Collections;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.hives.IHiveDrop;

public class HiveDrop implements IHiveDrop {

	private final IBeeDefinition beeTemplate;
	private final NonNullList<ItemStack> additional = NonNullList.create();
	private final double chance;
	private double ignobleShare = 0.0;

	public HiveDrop(double chance, IBeeDefinition beeTemplate, ItemStack... bonus) {
		this.beeTemplate = beeTemplate;
		this.chance = chance;

		Collections.addAll(this.additional, bonus);
	}

	public HiveDrop setIgnobleShare(double share) {
		this.ignobleShare = share;
		return this;
	}

	@Override
	public IBee getBeeType(IBlockReader world, BlockPos pos) {
		return beeTemplate.createIndividual();
	}

	@Override
	public NonNullList<ItemStack> getExtraItems(IBlockReader world, BlockPos pos, int fortune) {
		NonNullList<ItemStack> ret = NonNullList.create();
		for (ItemStack stack : additional) {
			ret.add(stack.copy());
		}

		return ret;
	}

	@Override
	public double getChance(IBlockReader world, BlockPos pos, int fortune) {
		return chance;
	}

	@Override
	public double getIgnobleChance(IBlockReader world, BlockPos pos, int fortune) {
		return ignobleShare;
	}
}
