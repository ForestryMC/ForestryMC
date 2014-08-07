/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture;

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

public class FlowerProviderNetherwart implements IFlowerProvider {

	@Override
	public boolean isAcceptedFlower(World world, IIndividual individual, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		return block == Blocks.nether_wart;
	}

	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable pollinatable) {
		return pollinatable.getPlantType().contains(EnumPlantType.Nether);
	}

	@Override
	public boolean growFlower(World world, IIndividual individual, int x, int y, int z) {

		Block block = world.getBlock(x, y, z);

		// We only grow netherwart, we don't plant it
		if (block != Blocks.nether_wart)
			return false;

		int meta = world.getBlockMetadata(x, y, z);
		if (meta >= 3)
			return false;

		if (meta == 2)
			meta = 3;
		else
			meta = 2;

		world.setBlockMetadataWithNotify(x, y, z, meta, Defaults.FLAG_BLOCK_SYNCH);
		return true;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("flowers.nether");
	}

	@Override
	public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
		return products;
	}

	@Override
	public ItemStack[] getItemStacks() {
		return new ItemStack[] { new ItemStack(Blocks.nether_wart) };
	}

}
