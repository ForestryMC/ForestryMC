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

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.core.config.Defaults;
import forestry.core.utils.StringUtil;

public class FlowerProviderJungle implements IFlowerProvider {

	@Override
	public boolean isAcceptedFlower(World world, IIndividual individual, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);

		// Specific check for flower pots.
		if (block == Blocks.flower_pot)
			return checkFlowerPot(meta);

		return block == Blocks.vine || (block == Blocks.tallgrass && meta == 2);
	}

	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable pollinatable) {
		EnumSet<EnumPlantType> types = pollinatable.getPlantType();
		return types.size() > 1 || !types.contains(EnumPlantType.Nether);
	}

	private boolean checkFlowerPot(int meta) {
		if (meta == 11)
			return true;
		else
			return false;
	}

	@Override
	public boolean growFlower(World world, IIndividual individual, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);

		if (block == Blocks.flower_pot)
			return growInPot(world, x, y, z);

		return false;
	}

	private boolean growInPot(World world, int x, int y, int z) {
		world.setBlock(x, y, z, Blocks.flower_pot, 11, Defaults.FLAG_BLOCK_SYNCH);
		return true;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("flowers.jungle");
	}

	@Override
	public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
		return products;
	}

	@Override
	public ItemStack[] getItemStacks() {
		return new ItemStack[] { new ItemStack(Blocks.vine), new ItemStack(Blocks.tallgrass, 1, 2) };
	}

}
