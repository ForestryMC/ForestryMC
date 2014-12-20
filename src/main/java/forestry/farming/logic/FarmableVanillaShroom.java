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
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.core.vect.Vect;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FarmableVanillaShroom extends FarmableGenericSapling {

	public FarmableVanillaShroom(Block sapling, int saplingMeta) {
		super(sapling, saplingMeta);
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);

		if (block != Blocks.brown_mushroom_block && block != Blocks.red_mushroom_block)
			return null;

		return new CropBlock(world, block, world.getBlockMetadata(x, y, z), new Vect(x, y, z));
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, int x, int y, int z) {
		int meta = 0;
		if (StackUtils.equals(Blocks.red_mushroom, germling))
			meta = 1;

		Proxies.common.addBlockPlaceEffects(world, x, y, z, Blocks.brown_mushroom, 0);
		return ForestryBlock.mushroom.setBlock(world, x, y, z, meta, Defaults.FLAG_BLOCK_SYNCH);
	}

}
