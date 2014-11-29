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

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IHiveDrop;
import forestry.api.genetics.IAllele;
import forestry.plugins.PluginApiculture;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;

public class HiveDrop implements IHiveDrop {

	private IAllele[] template;
	private ArrayList<ItemStack> additional = new ArrayList<ItemStack>();
	private int chance;
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
	
	private IBee createBee(World world) {
		return PluginApiculture.beeInterface.getBee(world, PluginApiculture.beeInterface.templateAsGenome(template));
	}
	
	@Override
	public ItemStack getPrincess(World world, int x, int y, int z, int fortune) {
		IBee bee = createBee(world);
		if(world.rand.nextFloat() < ignobleShare)
			bee.setIsNatural(false);

		return PluginApiculture.beeInterface.getMemberStack(bee, EnumBeeType.PRINCESS.ordinal());
	}

	@Override
	public ArrayList<ItemStack> getDrones(World world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(PluginApiculture.beeInterface.getMemberStack(createBee(world), EnumBeeType.DRONE.ordinal()));
		return ret;
	}

	@Override
	public ArrayList<ItemStack> getAdditional(World world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		for (ItemStack stack : additional)
			ret.add(stack.copy());

		return ret;
	}

	@Override
	public int getChance(World world, int x, int y, int z) {
		return chance;
	}

}
