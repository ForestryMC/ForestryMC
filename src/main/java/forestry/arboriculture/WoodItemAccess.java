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
package forestry.arboriculture;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.IWoodItemAccess;
import forestry.arboriculture.blocks.BlockArbFence;
import forestry.arboriculture.blocks.BlockArbLog;
import forestry.arboriculture.blocks.BlockArbPlanks;
import forestry.arboriculture.blocks.BlockArbSlab;
import forestry.arboriculture.blocks.BlockArbStairs;

public class WoodItemAccess implements IWoodItemAccess {
	private static final WoodMap logs = new WoodMap();
	private static final WoodMap planks = new WoodMap();
	private static final WoodMap slabs = new WoodMap();
	private static final WoodMap fences = new WoodMap();
	private static final WoodMap stairs = new WoodMap();

	private static class WoodMap {
		private final EnumMap<EnumWoodType, ItemStack> normal = new EnumMap<>(EnumWoodType.class);
		private final EnumMap<EnumWoodType, ItemStack> fireproof = new EnumMap<>(EnumWoodType.class);

		@Nonnull
		public EnumMap<EnumWoodType, ItemStack> get(boolean fireproof) {
			return fireproof ? this.fireproof : this.normal;
		}
	}

	public static void registerLogs(List<BlockArbLog> blocks) {
		for (BlockArbLog block : blocks) {
			register(block, logs);
		}
	}

	public static void registerPlanks(List<BlockArbPlanks> blocks) {
		for (BlockArbPlanks block : blocks) {
			register(block, planks);
		}
	}

	public static void registerSlabs(List<BlockArbSlab> blocks) {
		for (BlockArbSlab block : blocks) {
			register(block, slabs);
		}
	}

	public static void registerFences(List<BlockArbFence> blocks) {
		for (BlockArbFence block : blocks) {
			register(block, fences);
		}
	}

	public static void registerStairs(List<BlockArbStairs> blocks) {
		for (BlockArbStairs block : blocks) {
			register(block, stairs);
		}
	}

	private static <T extends Block & IWoodTyped> void register(T woodTyped, WoodMap woodMap) {
		boolean fireproof = woodTyped.isFireproof();
		EnumMap<EnumWoodType, ItemStack> registryMap = woodMap.get(fireproof);
		for (IBlockState blockState : woodTyped.getBlockState().getValidStates()) {
			int meta = woodTyped.getMetaFromState(blockState);
			EnumWoodType woodType = woodTyped.getWoodType(meta);
			ItemStack itemStack = new ItemStack(woodTyped, 1, meta);
			registryMap.put(woodType, itemStack);
		}
	}

	@Override
	public ItemStack getPlanks(EnumWoodType woodType, boolean fireproof) {
		return planks.get(fireproof).get(woodType).copy();
	}

	@Override
	public ItemStack getLog(EnumWoodType woodType, boolean fireproof) {
		return logs.get(fireproof).get(woodType).copy();
	}

	@Override
	public ItemStack getSlab(EnumWoodType woodType, boolean fireproof) {
		return slabs.get(fireproof).get(woodType).copy();
	}

	@Override
	public ItemStack getFence(EnumWoodType woodType, boolean fireproof) {
		return fences.get(fireproof).get(woodType).copy();
	}

	@Override
	public ItemStack getStairs(EnumWoodType woodType, boolean fireproof) {
		return stairs.get(fireproof).get(woodType).copy();
	}

}
