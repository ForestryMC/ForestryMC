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

import java.util.EnumMap;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.IWoodItemAccess;
import forestry.arboriculture.items.ItemBlockWood;

public class WoodItemAccess implements IWoodItemAccess {
	private static final EnumMap<EnumWoodType, ItemStack> logs = new EnumMap<>(EnumWoodType.class);
	private static final EnumMap<EnumWoodType, ItemStack> logsFireproof = new EnumMap<>(EnumWoodType.class);
	private static final EnumMap<EnumWoodType, ItemStack> planks = new EnumMap<>(EnumWoodType.class);
	private static final EnumMap<EnumWoodType, ItemStack> planksFireproof = new EnumMap<>(EnumWoodType.class);
	private static final EnumMap<EnumWoodType, ItemStack> slabs = new EnumMap<>(EnumWoodType.class);
	private static final EnumMap<EnumWoodType, ItemStack> slabsFireproof = new EnumMap<>(EnumWoodType.class);
	private static final EnumMap<EnumWoodType, ItemStack> fences = new EnumMap<>(EnumWoodType.class);
	private static final EnumMap<EnumWoodType, ItemStack> fencesFireproof = new EnumMap<>(EnumWoodType.class);
	private static final EnumMap<EnumWoodType, ItemStack> stairs = new EnumMap<>(EnumWoodType.class);
	private static final EnumMap<EnumWoodType, ItemStack> stairsFireproof = new EnumMap<>(EnumWoodType.class);

	private static ItemStack getStack(EnumWoodType woodType, Block block) {
		ItemStack itemStack = new ItemStack(block);
		ItemBlockWood.saveToItemStack(woodType, itemStack);
		return itemStack;
	}

	public static void registerLog(Block block, EnumWoodType woodType, boolean fireproof) {
		ItemStack itemStack = getStack(woodType, block);
		if (fireproof) {
			logsFireproof.put(woodType, itemStack);
		} else {
			logs.put(woodType, itemStack);
		}
	}

	public static void registerPlanks(Block block, EnumWoodType woodType, boolean fireproof) {
		ItemStack itemStack = getStack(woodType, block);
		if (fireproof) {
			planksFireproof.put(woodType, itemStack);
		} else {
			planks.put(woodType, itemStack);
		}
	}

	public static void registerSlab(Block block, EnumWoodType woodType, boolean fireproof) {
		ItemStack itemStack = getStack(woodType, block);
		if (fireproof) {
			slabsFireproof.put(woodType, itemStack);
		} else {
			slabs.put(woodType, itemStack);
		}
	}

	public static void registerFence(Block block, EnumWoodType woodType, boolean fireproof) {
		ItemStack itemStack = getStack(woodType, block);
		if (fireproof) {
			fencesFireproof.put(woodType, itemStack);
		} else {
			fences.put(woodType, itemStack);
		}
	}

	public static void registerStairs(Block block, EnumWoodType woodType, boolean fireproof) {
		ItemStack itemStack = getStack(woodType, block);
		if (fireproof) {
			stairsFireproof.put(woodType, itemStack);
		} else {
			stairs.put(woodType, itemStack);
		}
	}

	@Override
	public ItemStack getPlanks(EnumWoodType woodType, boolean fireproof) {
		if (fireproof) {
			return planksFireproof.get(woodType).copy();
		} else {
			return planks.get(woodType).copy();
		}
	}

	@Override
	public ItemStack getLog(EnumWoodType woodType, boolean fireproof) {
		if (fireproof) {
			return logsFireproof.get(woodType).copy();
		} else {
			return logs.get(woodType).copy();
		}
	}

	@Override
	public ItemStack getSlab(EnumWoodType woodType, boolean fireproof) {
		if (fireproof) {
			return slabsFireproof.get(woodType).copy();
		} else {
			return slabs.get(woodType).copy();
		}
	}

	@Override
	public ItemStack getFence(EnumWoodType woodType, boolean fireproof) {
		if (fireproof) {
			return fencesFireproof.get(woodType).copy();
		} else {
			return fences.get(woodType).copy();
		}
	}

	@Override
	public ItemStack getStairs(EnumWoodType woodType, boolean fireproof) {
		if (fireproof) {
			return stairsFireproof.get(woodType).copy();
		} else {
			return stairs.get(woodType).copy();
		}
	}

}
