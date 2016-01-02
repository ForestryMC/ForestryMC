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
package forestry.arboriculture.worldgen;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import forestry.api.arboriculture.EnumWoodType;
import forestry.api.world.ITreeGenData;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.core.worldgen.IBlockType;

public class BlockTypeWood implements IBlockType, ITreeBlockType {

	protected final ItemStack itemStack;
	protected int blockMeta;

	public BlockTypeWood(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public void setBlock(World world, ITreeGenData tree, BlockPos pos) {
		setBlock(world, pos);
	}

	@Override
	public void setBlock(World world, BlockPos pos) {
		ItemBlockWood.placeWood(itemStack, null, world, pos, Block.getBlockFromItem(itemStack.getItem()).getDefaultState().withProperty(EnumWoodType.WOODTYPE, EnumWoodType.values()[blockMeta]));
	}

	@Override
	public void setDirection(EnumFacing facing) {

	}
}
