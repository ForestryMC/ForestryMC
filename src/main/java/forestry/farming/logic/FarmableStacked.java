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

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.config.Defaults;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Vect;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FarmableStacked implements IFarmable {

	Block block;
	int matureHeight;

	public FarmableStacked(Block block, int matureHeight) {
		this.block = block;
		this.matureHeight = matureHeight;
	}

	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {
		return world.getBlock(x, y, z) == block;
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		if (world.getBlock(x, y + (matureHeight - 1), z) != block)
			return null;

		return new CropBlock(world, block, 0, new Vect(x, y + (matureHeight - 1), z));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return StackUtils.equals(block, itemstack);
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, int x, int y, int z) {
		return world.setBlock(x, y, z, block, 0, Defaults.FLAG_BLOCK_SYNCH);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
