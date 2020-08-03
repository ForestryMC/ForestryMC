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

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.Soil;
import forestry.core.utils.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

public abstract class FarmLogicWatered extends FarmLogicSoil {
    private static final FluidStack STACK_WATER = new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME);

    public FarmLogicWatered(IFarmProperties properties, boolean isManual) {
        super(properties, isManual);
    }

    @Override
    public boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
        if (maintainSoil(world, farmHousing, pos, direction, extent)) {
            return true;
        }

        if (!isManual && maintainWater(world, farmHousing, pos, direction, extent)) {
            return true;
        }

        return maintainCrops(world, farmHousing, pos.up(), direction, extent);

    }

    protected boolean isValidPosition(IFarmHousing housing, FarmDirection direction, BlockPos pos, CultivationType type) {
        return true;
    }

    private boolean maintainSoil(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
        if (!farmHousing.canPlantSoil(isManual)) {
            return false;
        }

        for (Soil soil : getSoils()) {
            NonNullList<ItemStack> resources = NonNullList.create();
            resources.add(soil.getResource());

            for (int i = 0; i < extent; i++) {
                BlockPos position = translateWithOffset(pos, direction, i);
                if (!world.isBlockLoaded(position)) {
                    break;
                }

                BlockState state = world.getBlockState(position);
                if (!isValidPosition(farmHousing, direction, position, CultivationType.SOIL)
                        || !BlockUtil.isBreakableBlock(state, world, pos)
                        || isAcceptedSoil(state)
                        || isWaterSourceBlock(world, position)
                        || !farmHousing.getFarmInventory().hasResources(resources)) {
                    continue;
                }

                BlockPos platformPosition = position.down();
                if (!farmHousing.isValidPlatform(world, platformPosition)) {
                    break;
                }

                if (!BlockUtil.isReplaceableBlock(state, world, position)) {
                    BlockUtil.getBlockDrops(world, position).forEach(farmHousing::addPendingProduct);
                    world.removeBlock(position, false);    //TODO
                    return trySetSoil(world, farmHousing, position, soil.getResource(), soil.getSoilState());
                }

                if (!isManual) {
                    if (trySetWater(world, farmHousing, position)) {
                        return true;
                    }

                    return trySetSoil(world, farmHousing, position, soil.getResource(), soil.getSoilState());
                }
            }
        }

        return false;
    }

    private boolean maintainWater(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
        // Still not done, check water then
        for (int i = 0; i < extent; i++) {
            BlockPos position = translateWithOffset(pos, direction, i);

            if (!world.isBlockLoaded(position)) {
                break;
            }

            if (!isValidPosition(farmHousing, direction, position, CultivationType.WATER)) {
                continue;
            }

            BlockPos platformPosition = position.down();
            if (!farmHousing.isValidPlatform(world, platformPosition)) {
                break;
            }

            if (BlockUtil.isBreakableBlock(world, pos) && trySetWater(world, farmHousing, position)) {
                return true;
            }
        }

        return false;
    }

    protected boolean maintainCrops(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
        return false;
    }

    private boolean trySetSoil(World world, IFarmHousing farmHousing, BlockPos position, ItemStack resource, BlockState ground) {
        NonNullList<ItemStack> resources = NonNullList.create();
        resources.add(resource);
        if (!farmHousing.getFarmInventory().hasResources(resources)) {
            return false;
        }
        if (!BlockUtil.setBlockWithPlaceSound(world, position, ground)) {
            return false;
        }
        farmHousing.getFarmInventory().removeResources(resources);
        return true;
    }

    private boolean trySetWater(World world, IFarmHousing farmHousing, BlockPos position) {
        if (isWaterSourceBlock(world, position)) {
            return false;
        }
        Pair<WaterStatus, BlockPos> waterPair = canPlaceWater(world, position);
        WaterStatus status = waterPair.getKey();
        if (status == WaterStatus.NO_WATER) {
            if (!farmHousing.hasLiquid(STACK_WATER)) {
                return false;
            }

            BlockUtil.getBlockDrops(world, position).forEach(farmHousing::addPendingProduct);
            BlockUtil.setBlockWithPlaceSound(world, position, Blocks.WATER.getDefaultState());
            farmHousing.removeLiquid(STACK_WATER);
            return true;
        } else if (status == WaterStatus.ICE) {
            BlockUtil.setBlockWithPlaceSound(world, waterPair.getValue(), Blocks.WATER.getDefaultState());
        }
        return false;
    }

    private Pair<WaterStatus, BlockPos> canPlaceWater(World world, BlockPos position) {
        // don't place water close to other water
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos offsetPosition = position.add(x, 0, z);
                if (isWaterSourceBlock(world, offsetPosition)) {
                    return Pair.of(WaterStatus.WATER_SOURCE, BlockPos.ZERO);
                }
                if (isIceBlock(world, offsetPosition) && !couldFlow(world, offsetPosition)) {
                    return Pair.of(WaterStatus.ICE, offsetPosition);
                }
            }
        }

        // don't place water if it can flow into blocks next to it
        if (couldFlow(world, position)) {
            return Pair.of(WaterStatus.AIR, BlockPos.ZERO);
        }

        return Pair.of(WaterStatus.NO_WATER, BlockPos.ZERO);
    }

    private boolean couldFlow(World world, BlockPos position) {
        for (int x = -1; x <= 1; x++) {
            BlockPos offsetPosition = position.add(x, 0, 0);
            if (world.isAirBlock(offsetPosition)) {
                return true;
            }
        }
        for (int z = -1; z <= 1; z++) {
            BlockPos offsetPosition = position.add(0, 0, z);
            if (world.isAirBlock(offsetPosition)) {
                return true;
            }
        }
        return false;
    }

    private enum WaterStatus {
        NO_WATER, WATER_SOURCE, AIR, ICE
    }

    public enum CultivationType {
        WATER, SOIL, CROP
    }

}
