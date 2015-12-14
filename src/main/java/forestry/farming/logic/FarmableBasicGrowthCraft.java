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

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.config.Constants;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.vect.Vect;

public class FarmableBasicGrowthCraft implements IFarmable {

	private final Block block;
	private final int matureMeta;
	private final boolean isRice;
	private final boolean isGrape;

	public FarmableBasicGrowthCraft(Block block, int matureMeta, boolean isRice, boolean isGrape) {
		this.block = block;
		this.matureMeta = matureMeta;
		this.isRice = isRice;
		this.isGrape = isGrape;
	}

	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {
		return world.getBlock(x, y, z) == block;
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z) != block) {
			return null;
		}
		if (world.getBlockMetadata(x, y, z) != matureMeta) {
			return null;
		}
		return new CropBasicGrowthCraft(world, block, matureMeta, new Vect(x, y, z), isRice, isGrape);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStackUtil.equals(block, itemstack);
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, int x, int y, int z) {
		return world.setBlock(x, y, z, block, 0, Constants.FLAG_BLOCK_SYNCH);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
