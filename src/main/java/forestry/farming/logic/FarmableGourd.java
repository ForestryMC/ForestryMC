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
package forestry.farming.logic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.config.Constants;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.vect.Vect;

public class FarmableGourd implements IFarmable {

	private final ItemStack seed;
	private final ItemStack stem;
	private final ItemStack fruit;

	public FarmableGourd(ItemStack seed, ItemStack stem, ItemStack fruit) {
		this.seed = seed;
		this.stem = stem;
		this.fruit = fruit;
	}

	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {
		if (world.isAirBlock(x, y, z)) {
			return false;
		}

		return ItemStackUtil.equals(world.getBlock(x, y, z), stem);
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		if (world.isAirBlock(x, y, z)) {
			return null;
		}

		if (!ItemStackUtil.equals(world.getBlock(x, y, z), fruit)) {
			return null;
		}

		if (world.getBlockMetadata(x, y, z) != fruit.getItemDamage()) {
			return null;
		}

		return new CropBlock(world, ItemStackUtil.getBlock(fruit), fruit.getItemDamage(), new Vect(x, y, z));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return seed.isItemEqual(itemstack);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, int x, int y, int z) {
		return world.setBlock(x, y, z, ItemStackUtil.getBlock(stem), 0, Constants.FLAG_BLOCK_SYNCH);
	}

}
