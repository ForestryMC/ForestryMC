/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.logic;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;
import forestry.plugins.PluginIC2;

public class CropRubber extends CropBlock {

	public CropRubber(World world, Block block, int meta, Vect position) {
		super(world, block, meta, position);
	}

	@Override
	protected Collection<ItemStack> harvestBlock(Vect pos) {
		Collection<ItemStack> harvested = new ArrayList<ItemStack>();
		harvested.add(PluginIC2.resin.copy());
		Proxies.common.addBlockDestroyEffects(world, pos.x, pos.y, pos.z, block, 0);
		world.setBlock(pos.x, pos.y, pos.z, block, meta + 6, Defaults.FLAG_BLOCK_SYNCH);
		return harvested;
	}

}
