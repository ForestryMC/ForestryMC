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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.core.vect.Vect;

public class FarmableVanillaShroom extends FarmableGenericSapling {

	public FarmableVanillaShroom(Block sapling, int saplingMeta) {
		super(sapling, saplingMeta);
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();

		if (block != Blocks.brown_mushroom_block && block != Blocks.red_mushroom_block) {
			return null;
		}

		return new CropBlock(world, block, world.getBlockMetadata(x, y, z), new Vect(pos));
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		int meta = 0;
		if (StackUtils.equals(Blocks.red_mushroom, germling)) {
			meta = 1;
		}

		Proxies.common.addBlockPlaceEffects(world, pos, Blocks.brown_mushroom.getDefaultState());
		return ForestryBlock.mushroom.setBlock(world, pos, meta, Defaults.FLAG_BLOCK_SYNCH);
	}

}
