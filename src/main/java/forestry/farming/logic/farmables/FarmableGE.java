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
package forestry.farming.logic.farmables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.ITreeRoot;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.farming.logic.crops.CropDestroy;

//import forestry.arboriculture.ModuleArboriculture;

public class FarmableGE implements IFarmable {

	@Override
	public boolean isSaplingAt(World world, BlockPos pos, BlockState blockState) {
		return false;
		//	TODO	return ModuleArboriculture.getBlocks().saplingGE == blockState.getBlock();
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos, BlockState blockState) {
		Block block = blockState.getBlock();

		//TODO - check
		if (!block.getTags().contains(new ResourceLocation("forge", "wood"))) {
			return null;
		}

		return new CropDestroy(world, blockState, pos, null);
	}

	@Override
	public boolean plantSaplingAt(PlayerEntity player, ItemStack germling, World world, BlockPos pos) {
		ITreeRoot treeRoot = TreeManager.treeRoot;

		ITree tree = treeRoot.create(germling).orElse(null);
		return tree != null && treeRoot.plantSapling(world, tree, player.getGameProfile(), pos);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return TreeManager.treeRoot.isMember(itemstack);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
