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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.arboriculture.PluginArboriculture;
import forestry.core.utils.GeneticsUtil;

public class FarmableGE implements IFarmable {

	@Override
	public boolean isSaplingAt(World world, BlockPos pos) {

		if (world.isAirBlock(pos)) {
			return false;
		}

		Block block = world.getBlockState(pos).getBlock();
		return PluginArboriculture.blocks.saplingGE == block;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
		Block block = blockState.getBlock();

		if (!block.isWood(world, pos)) {
			return null;
		}

		return new CropDestroy(world, blockState, pos, null);
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		ITree tree = TreeManager.treeRoot.getMember(germling);
		if (tree == null) {
			return false;
		}
		return TreeManager.treeRoot.plantSapling(world, tree, player.getGameProfile(), pos);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		itemstack = GeneticsUtil.convertSaplingToGeneticEquivalent(itemstack);
		return TreeManager.treeRoot.isMember(itemstack);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
