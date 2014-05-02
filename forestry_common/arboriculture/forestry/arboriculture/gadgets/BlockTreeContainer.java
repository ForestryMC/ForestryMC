/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.gadgets;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockTreeContainer extends BlockContainer {

	public BlockTreeContainer(Material material) {
		super(material);
		setTickRandomly(true);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {

		if (world.rand.nextFloat() > 0.1)
			return;

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileTreeContainer))
			return;

		((TileTreeContainer) tile).onBlockTick();
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

}
