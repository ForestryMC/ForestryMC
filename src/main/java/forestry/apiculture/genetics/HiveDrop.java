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
import net.minecraft.world.World;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IHiveDrop;

public class HiveDrop implements IHiveDrop {

	private final IBeeDefinition beeTemplate;
	private final ArrayList<ItemStack> additional = new ArrayList<>();
	private final int chance;
	private float ignobleShare = 0.0f;

	public HiveDrop(int chance, IBeeDefinition beeTemplate, ItemStack... bonus) {
		this.beeTemplate = beeTemplate;
		this.chance = chance;

		Collections.addAll(this.additional, bonus);
	}

	public HiveDrop setIgnobleShare(float share) {
		this.ignobleShare = share;
		return this;
	}
	
	@Override
	public ItemStack getPrincess(World world, int x, int y, int z, int fortune) {
		IBee bee = beeTemplate.getIndividual();
		if (world.rand.nextFloat() < ignobleShare) {
			bee.setIsNatural(false);
		}

		return BeeManager.beeRoot.getMemberStack(bee, EnumBeeType.PRINCESS.ordinal());
	}

	@Override
	public List<ItemStack> getDrones(World world, int x, int y, int z, int fortune) {
		ItemStack drone = beeTemplate.getMemberStack(EnumBeeType.DRONE);
		return Collections.singletonList(drone);
	}

	@Override
	public ArrayList<ItemStack> getAdditional(World world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<>();
		for (ItemStack stack : additional) {
			ret.add(stack.copy());
		}

		return ret;
	}

	@Override
	public int getChance(World world, int x, int y, int z) {
		return chance;
	}

}
