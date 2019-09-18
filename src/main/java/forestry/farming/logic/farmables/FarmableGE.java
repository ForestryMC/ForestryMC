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

import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.TreeManager;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.api.genetics.AlleleManager;
import forestry.arboriculture.ModuleArboriculture;
import forestry.core.utils.datastructures.ItemStackMap;
import forestry.farming.logic.crops.CropDestroy;

public class FarmableGE implements IFarmable {

	//StackMap used because a normal HashSet didn't seem to work
	//TODO use items instead and normal set if this is still used in 1.14+ since items are flattened
	private final Set<ItemStack> windfall = Collections.newSetFromMap(new ItemStackMap<>());

	//TODO would be nice to make this class more granular so windfall and germling checks could be more specific
	public FarmableGE() {
		windfall.addAll(AlleleManager.alleleRegistry.getRegisteredFruitFamilies().values().stream()
				.map(TreeManager.treeRoot::getFruitProvidersForFruitFamily)
				.flatMap(Collection::stream)
				.map(p -> Sets.union(p.getProducts().keySet(), p.getSpecialty().keySet()))
				.flatMap(Collection::stream)
				.collect(Collectors.toSet()));
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos, IBlockState blockState) {
		return ModuleArboriculture.getBlocks().saplingGE == blockState.getBlock();
	}

	@Override
	@Nullable
	public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
		Block block = blockState.getBlock();

		if (!block.isWood(world, pos)) {
			return null;
		}

		return new CropDestroy(world, blockState, pos, null);
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		ITreeRoot treeRoot = TreeManager.treeRoot;

		ITree tree = treeRoot.getMember(germling);
		return tree != null && treeRoot.plantSapling(world, tree, player.getGameProfile(), pos);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return TreeManager.treeRoot.isMember(itemstack);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return windfall.contains(itemstack);
	}

}
