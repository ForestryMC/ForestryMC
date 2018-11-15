/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.farming.logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.farming.logic.crops.CropRubber;
import forestry.plugins.PluginIC2;
import forestry.plugins.PluginTechReborn;

public class FarmLogicRubber extends FarmLogic {

	private boolean active = true;

	public FarmLogicRubber(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);
		if ((PluginIC2.rubberWood == null || PluginIC2.resin == null) &&
			PluginTechReborn.rubberItemsSuccess()) {
			Log.warning("Failed to init a farm logic {} since IC2 rubber wood or resin were not found", getClass().getName());
			active = false;
		}
	}

	@Override
	public ItemStack getIconItemStack() {
		if (ModUtil.isModLoaded(PluginIC2.MOD_ID)) {
			return PluginIC2.resin;
		} else if (ModUtil.isModLoaded(PluginTechReborn.MOD_ID)) {
			return PluginTechReborn.sap;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public String getUnlocalizedName() {
		return "for.farm.rubber";
	}

	@Override
	public int getFertilizerConsumption() {
		return 40;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (5 * hydrationModifier);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isAcceptedWindfall(ItemStack stack) {
		return false;
	}

	@Override
	public NonNullList<ItemStack> collect(World world, IFarmHousing farmHousing) {
		return NonNullList.create();
	}

	@Override
	public boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		return false;
	}

	private final Map<BlockPos, Integer> lastExtents = new HashMap<>();

	@Override
	public Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent) {
		if (!active) {
			return null;
		}

		if (!lastExtents.containsKey(pos)) {
			lastExtents.put(pos, 0);
		}

		int lastExtent = lastExtents.get(pos);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		BlockPos position = translateWithOffset(pos.up(), direction, lastExtent);
		Collection<ICrop> crops = getHarvestBlocks(world, position);
		lastExtent++;
		lastExtents.put(pos, lastExtent);

		return crops;
	}

	private Collection<ICrop> getHarvestBlocks(World world, BlockPos position) {
		Stack<ICrop> crops = new Stack<>();

		for (int j = 0; j < 10; j++) {
			BlockPos candidate = position.add(0, j, 0);

			if (!world.isBlockLoaded(candidate)) {
				return crops;
			}

			IBlockState blockState = world.getBlockState(candidate);
			Block block = blockState.getBlock();
			if ((PluginIC2.rubberWood != null && !ItemStackUtil.equals(block, PluginIC2.rubberWood)) &&
				(PluginTechReborn.RUBBER_WOOD != null && !ItemStackUtil.equals(block, PluginTechReborn.RUBBER_WOOD))) {
				break;
			}

			if (CropRubber.hasRubberToHarvest(blockState)) {
				crops.push(new CropRubber(world, blockState, candidate));
				break;
			}
		}

		return crops;
	}

}
