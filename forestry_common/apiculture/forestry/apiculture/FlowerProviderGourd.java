/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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

public class FlowerProviderGourd implements IFlowerProvider {

	@Override
	public boolean isAcceptedFlower(World world, IIndividual individual, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		return block == Blocks.pumpkin_stem || block == Blocks.melon_stem;
	}

	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable pollinatable) {
		EnumSet<EnumPlantType> types = pollinatable.getPlantType();
		return types.size() > 1 || !types.contains(EnumPlantType.Nether);
	}

	@Override
	public boolean growFlower(World world, IIndividual individual, int x, int y, int z) {

		Block block = world.getBlock(x, y, z);

		// We only grow wheat, we don't plant it
		if (block != Blocks.pumpkin_stem && block != Blocks.melon_stem)
			return false;

		int meta = world.getBlockMetadata(x, y, z);
		if (meta > 6)
			return false;

		if (meta < 6)
			meta += 2;
		else
			meta = 7;

		world.setBlockMetadataWithNotify(x, y, z, meta, Defaults.FLAG_BLOCK_SYNCH);
		return true;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("flowers.gourd");
	}

	@Override
	public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
		return products;
	}

	@Override
	public ItemStack[] getItemStacks() {
		return new ItemStack[] { new ItemStack(Blocks.wheat, 1, 8) };
	}

}
