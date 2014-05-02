/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.logic;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Vect;

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
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {
		int meta = 0;
		if (StackUtils.equals(Blocks.red_mushroom, germling))
			meta = 1;

		Proxies.common.addBlockPlaceEffects(world, x, y, z, Blocks.brown_mushroom, 0);
		return world.setBlock(x, y, z, ForestryBlock.mushroom, meta, Defaults.FLAG_BLOCK_SYNCH);
	}

}
