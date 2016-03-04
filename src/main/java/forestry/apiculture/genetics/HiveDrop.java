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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IHiveDrop;

public class HiveDrop implements IHiveDrop {

	private final IBeeDefinition beeTemplate;
	private final ArrayList<ItemStack> additional = new ArrayList<>();
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
	public IBee getBeeType(IBlockAccess world, BlockPos pos) {
		return beeTemplate.getIndividual();
	}

	@Override
	public List<ItemStack> getExtraItems(IBlockAccess world, BlockPos pos, int fortune) {
		List<ItemStack> ret = new ArrayList<>();
		for (ItemStack stack : additional) {
			ret.add(stack.copy());
		}

		return ret;
	}

	@Override
	public double getChance(IBlockAccess world, BlockPos pos, int fortune) {
		return chance;
	}

	@Override
	public double getIgnobleChance(IBlockAccess world, BlockPos pos, int fortune) {
		return ignobleShare;
	}
}
