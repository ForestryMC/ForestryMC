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
package forestry.core.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import genetics.api.GeneticsAPI;

import forestry.api.arboriculture.TreeManager;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.SlotUtil;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

public class InventoryAnalyzer extends InventoryAdapterTile<TileAnalyzer> {
	public static final short SLOT_ANALYZE = 0;
	public static final short SLOT_CAN = 1;
	public static final short SLOT_INPUT_1 = 2;
	public static final short SLOT_INPUT_COUNT = 6;
	public static final short SLOT_OUTPUT_1 = 8;
	public static final short SLOT_OUTPUT_COUNT = 4;

	public InventoryAnalyzer(TileAnalyzer analyzer) {
		super(analyzer, 12, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (SlotUtil.isSlotInRange(slotIndex, SLOT_INPUT_1, SLOT_INPUT_COUNT)) {
			return GeneticsAPI.apiInstance.getRootHelper().isIndividual(itemStack) || GeneticsUtil.getGeneticEquivalent(itemStack).isPresent();
		} else if (slotIndex == SLOT_CAN) {
			LazyOptional<FluidStack> fluid = FluidUtil.getFluidContained(itemStack);
			return fluid.map(f -> tile.getTankManager().canFillFluidType(f)).orElse(false);
		}

		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, Direction side) {
		return SlotUtil.isSlotInRange(slotIndex, SLOT_OUTPUT_1, SLOT_OUTPUT_COUNT);
	}

	@Override
	public void setInventorySlotContents(int slotId, ItemStack itemStack) {
		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE) && !TreeManager.treeRoot.isMember(itemStack)) {
			itemStack = GeneticsUtil.convertToGeneticEquivalent(itemStack);
		}

		super.setInventorySlotContents(slotId, itemStack);
	}
}
