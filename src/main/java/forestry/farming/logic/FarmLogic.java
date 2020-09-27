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

import com.google.common.base.Predicate;
import forestry.api.farming.*;
import forestry.core.utils.VectUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public abstract class FarmLogic implements IFarmLogic {
    private final EntitySelectorFarm entitySelectorFarm;
    protected final IFarmProperties properties;
    protected final boolean isManual;

    public FarmLogic(IFarmProperties properties, boolean isManual) {
        this.properties = properties;
        this.isManual = isManual;
        this.entitySelectorFarm = new EntitySelectorFarm(properties);
    }

    protected Collection<IFarmable> getFarmables() {
        return properties.getFarmables();
    }

    protected Collection<Soil> getSoils() {
        return properties.getSoils();
    }

    @Override
    public IFarmProperties getProperties() {
        return properties;
    }

    @Override
    public boolean isManual() {
        return isManual;
    }

    @Override
    public Collection<ICrop> harvest(
            World world,
            IFarmHousing housing,
            FarmDirection direction,
            int extent,
            BlockPos pos
    ) {
        Stack<ICrop> crops = new Stack<>();
        for (int i = 0; i < extent; i++) {
            BlockPos position = translateWithOffset(pos.up(), direction, i);
            ICrop crop = getCrop(world, position);
            if (crop != null) {
                crops.push(crop);
            }
        }
        return crops;
    }

    @Nullable
    protected ICrop getCrop(World world, BlockPos position) {
        if (!world.isBlockLoaded(position) || world.isAirBlock(position)) {
            return null;
        }
        BlockState blockState = world.getBlockState(position);
        for (IFarmable seed : getFarmables()) {
            ICrop crop = seed.getCropAt(world, position, blockState);
            if (crop != null) {
                return crop;
            }
        }
        return null;
    }

    @Deprecated
    public boolean isAcceptedWindfall(ItemStack stack) {
        return false;
    }

    protected final boolean isWaterSourceBlock(World world, BlockPos position) {
        if (!world.isBlockLoaded(position)) {
            return false;
        }
        BlockState blockState = world.getBlockState(position);
        Block block = blockState.getBlock();
        return block == Blocks.WATER;
    }

    protected final boolean isIceBlock(World world, BlockPos position) {
        if (!world.isBlockLoaded(position)) {
            return false;
        }
        BlockState blockState = world.getBlockState(position);
        Block block = blockState.getBlock();
        return block == Blocks.ICE;
    }

    protected final BlockPos translateWithOffset(BlockPos pos, FarmDirection farmDirection, int step) {
        return VectUtil.scale(farmDirection.getFacing().getDirectionVec(), step).add(pos);
    }

    private static AxisAlignedBB getHarvestBox(World world, IFarmHousing farmHousing, boolean toWorldHeight) {
        BlockPos coords = farmHousing.getCoords();
        Vector3i area = farmHousing.getArea();
        Vector3i offset = farmHousing.getOffset();

        BlockPos min = coords.add(offset);
        BlockPos max = min.add(area);

        int maxY = max.getY();
        if (toWorldHeight) {
            maxY = world.getHeight();
        }

        return new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), maxY, max.getZ());
    }

    protected NonNullList<ItemStack> collectEntityItems(World world, IFarmHousing farmHousing, boolean toWorldHeight) {
        AxisAlignedBB harvestBox = getHarvestBox(world, farmHousing, toWorldHeight);

        List<ItemEntity> entityItems = world.getEntitiesWithinAABB(ItemEntity.class, harvestBox, entitySelectorFarm);
        NonNullList<ItemStack> stacks = NonNullList.create();
        for (ItemEntity entity : entityItems) {
            ItemStack contained = entity.getItem();
            stacks.add(contained.copy());
            entity.remove();
        }
        return stacks;
    }

    // for debugging
    @Override
    public String toString() {
        return properties.getTranslationKey();
    }

    private static class EntitySelectorFarm implements Predicate<ItemEntity> {
        private final IFarmProperties properties;

        public EntitySelectorFarm(IFarmProperties properties) {
            this.properties = properties;
        }

        @Override
        public boolean apply(@Nullable ItemEntity entity) {
            if (entity == null || !entity.isAlive()) {
                return false;
            }

            //TODO not sure if this key still exists
            if (entity.getPersistentData().getBoolean("PreventRemoteMovement")) {
                return false;
            }

            ItemStack contained = entity.getItem();
            return properties.isAcceptedSeedling(contained) || properties.isAcceptedWindfall(contained);
        }
    }
}
