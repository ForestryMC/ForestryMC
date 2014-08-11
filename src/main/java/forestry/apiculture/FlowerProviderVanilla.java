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
package forestry.apiculture;

import java.util.Collections;
import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.core.config.Defaults;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;

public class FlowerProviderVanilla implements IFlowerProvider {

	@Override
	public boolean isAcceptedFlower(World world, IIndividual individual, int x, int y, int z) {

		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);

		// Specific check for flower pots.
		if (block == Blocks.flower_pot)
			return checkFlowerPot(meta);

		ItemStack flower = new ItemStack(block, 1, meta);

		for (ItemStack stack : FlowerManager.plainFlowers)
			if (flower.isItemEqual(stack))
				return true;

		return false;
	}

	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable pollinatable) {
		EnumSet<EnumPlantType> types = pollinatable.getPlantType();
		return types.size() > 1 || !types.contains(EnumPlantType.Nether);
	}

	private boolean checkFlowerPot(int meta) {
		if (meta == 1 || meta == 2)
			return true;
		else
			return false;
	}

	@Override
	public boolean growFlower(World world, IIndividual individual, int x, int y, int z) {

		Block block = world.getBlock(x, y, z);

		if (!block.isAir(world, x, y, z))
			if (block == Blocks.flower_pot  && world.getBlockMetadata(x, y, z) == 0)
				return growInPot(world, x, y, z);
			else
				return false;

		// Check ground
		Block ground = world.getBlock(x, y - 1, z);

		if (ground != Blocks.dirt && ground != Blocks.grass)
			return false;

		// Determine flower to plant
		Collections.shuffle(FlowerManager.plainFlowers);
		ItemStack flower = FlowerManager.plainFlowers.get(world.rand.nextInt(FlowerManager.plainFlowers.size() - 1));
		world.setBlock(x, y, z, StackUtils.getBlock(flower), flower.getItemDamage(), Defaults.FLAG_BLOCK_SYNCH);
		return true;
	}

	private boolean growInPot(World world, int x, int y, int z) {
		if (world.rand.nextBoolean())
			world.setBlock(x, y, z, Blocks.flower_pot, 1, Defaults.FLAG_BLOCK_SYNCH);
		else
			world.setBlock(x, y, z, Blocks.flower_pot, 2, Defaults.FLAG_BLOCK_SYNCH);
		return true;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("flowers.vanilla");
	}

	@Override
	public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
		return products;
	}

	@Override
	public ItemStack[] getItemStacks() {
		return FlowerManager.plainFlowers.toArray(new ItemStack[0]);
	}

}
