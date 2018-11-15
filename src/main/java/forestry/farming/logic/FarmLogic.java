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

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.api.farming.ISoil;
import forestry.core.utils.Translator;
import forestry.core.utils.VectUtil;

public abstract class FarmLogic implements IFarmLogic {
	private final EntitySelectorFarm entitySelectorFarm = new EntitySelectorFarm(this);
	protected final IFarmProperties properties;
	protected final boolean isManual;

	public FarmLogic(IFarmProperties properties, boolean isManual) {
		this.properties = properties;
		this.isManual = isManual;
	}

	protected Collection<IFarmable> getFarmables() {
		return properties.getFarmables();
	}

	protected Collection<ISoil> getSoils() {
		return properties.getSoils();
	}

	@Override
	public String getName() {
		String unformatted = isManual ? "for.farm.grammar.manual" : "for.farm.grammar.managed";
		return Translator.translateToLocalFormatted(unformatted, Translator.translateToLocal(getUnlocalizedName()));
	}

	@Override
	public FarmLogic setManual(boolean flag) {
		return this;
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
	public Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent) {
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
		IBlockState blockState = world.getBlockState(position);
		for (IFarmable seed : getFarmables()) {
			ICrop crop = seed.getCropAt(world, position, blockState);
			if (crop != null) {
				return crop;
			}
		}
		return null;
	}

	public abstract boolean isAcceptedWindfall(ItemStack stack);

	protected final boolean isWaterSourceBlock(World world, BlockPos position) {
		if (!world.isBlockLoaded(position)) {
			return false;
		}
		IBlockState blockState = world.getBlockState(position);
		Block block = blockState.getBlock();
		return block == Blocks.WATER;
	}

	protected final BlockPos translateWithOffset(BlockPos pos, FarmDirection farmDirection, int step) {
		return VectUtil.scale(farmDirection.getFacing().getDirectionVec(), step).add(pos);
	}

	private static AxisAlignedBB getHarvestBox(World world, IFarmHousing farmHousing, boolean toWorldHeight) {
		BlockPos coords = farmHousing.getCoords();
		Vec3i area = farmHousing.getArea();
		Vec3i offset = farmHousing.getOffset();

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

		List<EntityItem> entityItems = world.getEntitiesWithinAABB(EntityItem.class, harvestBox, entitySelectorFarm);
		NonNullList<ItemStack> stacks = NonNullList.create();
		for (EntityItem entity : entityItems) {
			ItemStack contained = entity.getItem();
			stacks.add(contained.copy());
			entity.setDead();
		}
		return stacks;
	}

	// for debugging
	@Override
	public String toString() {
		return getName();
	}

	private static class EntitySelectorFarm implements Predicate<EntityItem> {
		private final FarmLogic farmLogic;

		public EntitySelectorFarm(FarmLogic farmLogic) {
			this.farmLogic = farmLogic;
		}

		@Override
		public boolean apply(@Nullable EntityItem entity) {
			if (entity == null || entity.isDead) {
				return false;
			}

			if (entity.getEntityData().getBoolean("PreventRemoteMovement")) {
				return false;
			}

			ItemStack contained = entity.getItem();
			return farmLogic.isAcceptedGermling(contained) || farmLogic.isAcceptedWindfall(contained);
		}
	}
}
