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

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.config.Defaults;
import forestry.core.utils.StackUtils;
import forestry.core.vect.Vect;

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
	public boolean isSaplingAt(World world, BlockPos pos) {
		if (world.isAirBlock(pos)) {
			return false;
		}

		return StackUtils.equals(world.getBlockState(pos).getBlock(), stem);
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (world.isAirBlock(pos)) {
			return null;
		}

		if (!StackUtils.equals(state.getBlock(), fruit)) {
			return null;
		}

		if (state.getBlock().getMetaFromState(state) != fruit.getItemDamage()) {
			return null;
		}

		return new CropBlock(world, StackUtils.getBlock(fruit), fruit.getItemDamage(), new Vect(pos));
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
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		return world.setBlockState(pos, StackUtils.getBlock(stem).getStateFromMeta(0), Defaults.FLAG_BLOCK_SYNCH);
	}

}
