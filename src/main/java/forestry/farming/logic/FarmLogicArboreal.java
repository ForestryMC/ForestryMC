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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;

public class FarmLogicArboreal extends FarmLogicHomogeneous {

	@Nullable
	private List<IFarmable> farmables;

	public FarmLogicArboreal(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);
	}

	@Override
	public List<IFarmable> getFarmables() {
		if (farmables == null) {
			this.farmables = new ArrayList<>(properties.getFarmables());
		}
		return farmables;
	}

	@Override
	public NonNullList<ItemStack> collect(Level world, IFarmHousing farmHousing) {
		return collectEntityItems(world, farmHousing, true);
	}

	@Override
	public Collection<ICrop> harvest(Level world, IFarmHousing farmHousing, FarmDirection direction, int extent, BlockPos pos) {
		BlockPos position = farmHousing.getValidPosition(direction, pos, extent, pos.above());
		Collection<ICrop> crops = harvestBlocks(world, position);
		farmHousing.increaseExtent(direction, pos, extent);

		return crops;
	}

	private Collection<ICrop> harvestBlocks(Level world, BlockPos position) {
		// Determine what type we want to harvest.
		IFarmable farmable = getFarmableForBlock(world, position, getFarmables());
		if (farmable == null) {
			return Collections.emptyList();
		}

		// get all crops of the same type that are connected to the first one
		Stack<BlockPos> knownCropPositions = new Stack<>();
		knownCropPositions.add(position);

		Set<BlockPos> checkedBlocks = new HashSet<>();
		Stack<ICrop> crops = new Stack<>();

		while (!knownCropPositions.empty()) {
			BlockPos knownCropPos = knownCropPositions.pop();
			for (BlockPos mutable : BlockPos.betweenClosed(knownCropPos.offset(-1, -1, -1), knownCropPos.offset(1, 1, 1))) {
				if (!world.hasChunkAt(mutable)) {
					return crops;
				}

				BlockPos candidate = mutable.immutable();
				if (!checkedBlocks.contains(candidate)) {
					checkedBlocks.add(candidate);

					BlockState blockState = world.getBlockState(candidate);
					ICrop crop = farmable.getCropAt(world, candidate, blockState);
					if (crop != null) {
						crops.push(crop);
						knownCropPositions.push(candidate);
					}
				}
			}
		}

		return crops;
	}

	@Nullable
	private static IFarmable getFarmableForBlock(Level world, BlockPos position, Collection<IFarmable> farmables) {
		if (world.isEmptyBlock(position)) {
			return null;
		}
		BlockState blockState = world.getBlockState(position);
		for (IFarmable farmable : farmables) {
			ICrop crop = farmable.getCropAt(world, position, blockState);
			if (crop != null) {
				return farmable;
			}
		}
		return null;
	}

	@Override
	protected boolean maintainSeedlings(Level world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);

			if (world.isEmptyBlock(position)) {
				BlockPos soilPosition = position.below();
				BlockState soilState = world.getBlockState(soilPosition);
				if (isAcceptedSoil(soilState)) {
					return plantSapling(world, farmHousing, position, direction);
				}
			}
		}
		return false;
	}

	private boolean plantSapling(Level world, IFarmHousing farmHousing, BlockPos position, FarmDirection direction) {
		Collections.shuffle(getFarmables());
		for (IFarmable candidate : getFarmables()) {
			if (farmHousing.plantGermling(candidate, world, position, direction)) {
				return true;
			}
		}

		return false;
	}

}
