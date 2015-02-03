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
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IHiveDrop;
import forestry.api.genetics.IAllele;
import forestry.plugins.PluginApiculture;

public class HiveDrop implements IHiveDrop {

	private final IAllele[] template;
	private final ArrayList<ItemStack> additional = new ArrayList<ItemStack>();
	private final int chance;
	private float ignobleShare = 0.0f;

	public HiveDrop(IAllele[] template, ItemStack[] bonus, int chance) {
		this.template = template;
		this.chance = chance;

		Collections.addAll(this.additional, bonus);
	}

	public HiveDrop setIgnobleShare(float share) {
		this.ignobleShare = share;
		return this;
	}
	
	private IBee createBee(IBlockAccess world) {
		return PluginApiculture.beeInterface.getBee(world, PluginApiculture.beeInterface.templateAsGenome(template));
	}
	
	@Override
	public ItemStack getPrincess(IBlockAccess world, BlockPos pos, int fortune) {
		IBee bee = createBee(world);
		if (new Random().nextFloat() < ignobleShare) {
			bee.setIsNatural(false);
		}

		return PluginApiculture.beeInterface.getMemberStack(bee, EnumBeeType.PRINCESS.ordinal());
	}

	@Override
	public List<ItemStack> getDrones(IBlockAccess world, BlockPos pos, int fortune) {
		List<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(PluginApiculture.beeInterface.getMemberStack(createBee(world), EnumBeeType.DRONE.ordinal()));
		return ret;
	}

	@Override
	public List<ItemStack> getAdditional(IBlockAccess world, BlockPos pos, int fortune) {
		List<ItemStack> ret = new ArrayList<ItemStack>();
		for (ItemStack stack : additional) {
			ret.add(stack.copy());
		}

		return ret;
	}

	@Override
	public int getChance(IBlockAccess world, BlockPos pos) {
		return chance;
	}

}
