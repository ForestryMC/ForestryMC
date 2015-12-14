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

import net.minecraftforge.fluids.Fluid;

import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.core.fluids.FluidHelper;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.SlotUtil;
import forestry.plugins.PluginManager;

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
			return AlleleManager.alleleRegistry.isIndividual(itemStack) || GeneticsUtil.getGeneticEquivalent(itemStack) != null;
		} else if (slotIndex == SLOT_CAN) {
			Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
			return tile.getTankManager().accepts(fluid);
		}

		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
		return SlotUtil.isSlotInRange(slotIndex, SLOT_OUTPUT_1, SLOT_OUTPUT_COUNT);
	}

	@Override
	public void setInventorySlotContents(int slotId, ItemStack itemStack) {
		if (PluginManager.Module.ARBORICULTURE.isEnabled() && !TreeManager.treeRoot.isMember(itemStack)) {
			ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(itemStack);
			if (ersatz != null) {
				itemStack = ersatz;
			}
		}

		super.setInventorySlotContents(slotId, itemStack);
	}
}
