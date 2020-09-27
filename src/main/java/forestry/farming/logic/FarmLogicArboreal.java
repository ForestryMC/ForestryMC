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

import forestry.api.farming.*;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

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
    public NonNullList<ItemStack> collect(World world, IFarmHousing farmHousing) {
        return collectEntityItems(world, farmHousing, true);
    }

    @Override
    public Collection<ICrop> harvest(
            World world,
            IFarmHousing farmHousing,
            FarmDirection direction,
            int extent,
            BlockPos pos
    ) {
        BlockPos position = farmHousing.getValidPosition(direction, pos, extent, pos.up());
        Collection<ICrop> crops = harvestBlocks(world, position);
        farmHousing.increaseExtent(direction, pos, extent);

        return crops;
    }

    private Collection<ICrop> harvestBlocks(World world, BlockPos position) {
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
            for (BlockPos mutable : BlockPos.getAllInBoxMutable(
                    knownCropPos.add(-1, -1, -1),
                    knownCropPos.add(1, 1, 1)
            )) {
                if (!world.isBlockLoaded(mutable)) {
                    return crops;
                }

                BlockPos candidate = mutable.toImmutable();
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
    private static IFarmable getFarmableForBlock(World world, BlockPos position, Collection<IFarmable> farmables) {
        if (world.isAirBlock(position)) {
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
    protected boolean maintainSeedlings(
            World world,
            IFarmHousing farmHousing,
            BlockPos pos,
            FarmDirection direction,
            int extent
    ) {
        for (int i = 0; i < extent; i++) {
            BlockPos position = translateWithOffset(pos, direction, i);

            if (world.isAirBlock(position)) {
                BlockPos soilPosition = position.down();
                BlockState soilState = world.getBlockState(soilPosition);
                if (isAcceptedSoil(soilState)) {
                    return plantSapling(world, farmHousing, position, direction);
                }
            }
        }
        return false;
    }

    private boolean plantSapling(World world, IFarmHousing farmHousing, BlockPos position, FarmDirection direction) {
        Collections.shuffle(getFarmables());
        for (IFarmable candidate : getFarmables()) {
            if (farmHousing.plantGermling(candidate, world, position, direction)) {
                return true;
            }
        }

        return false;
    }

}
