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

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.core.tiles.TileEscritoire;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.SlotUtil;

public class InventoryEscritoire extends InventoryAdapterTile<TileEscritoire> {
	public static final short SLOT_ANALYZE = 0;
	public static final short SLOT_RESULTS_1 = 1;
	public static final short SLOTS_RESULTS_COUNT = 6;
	public static final short SLOT_INPUT_1 = 7;
	public static final short SLOTS_INPUT_COUNT = 5;

	public InventoryEscritoire(TileEscritoire escritoire) {
		super(escritoire, 12, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex >= SLOT_INPUT_1 && slotIndex < SLOT_INPUT_1 + tile.getGame().getSampleSize(SLOTS_INPUT_COUNT)) {
			ItemStack specimen = getStackInSlot(SLOT_ANALYZE);
			if (specimen == null) {
				return false;
			}
			IIndividual individual = AlleleManager.alleleRegistry.getIndividual(specimen);
			return individual != null && individual.getGenome().getPrimary().getResearchSuitability(itemStack) > 0;
		}

		if (slotIndex == SLOT_ANALYZE) {
			return AlleleManager.alleleRegistry.isIndividual(itemStack);
		}

		return false;
	}

	@Override
	public boolean isLocked(int slotIndex) {
		if (slotIndex == SLOT_ANALYZE) {
			return false;
		}

		if (getStackInSlot(SLOT_ANALYZE) == null) {
			return true;
		}

		if (SlotUtil.isSlotInRange(slotIndex, SLOT_INPUT_1, SLOTS_INPUT_COUNT)) {
			if (slotIndex >= SLOT_INPUT_1 + tile.getGame().getSampleSize(SLOTS_INPUT_COUNT)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
		return SlotUtil.isSlotInRange(slotIndex, SLOT_RESULTS_1, SLOTS_RESULTS_COUNT);
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		super.setInventorySlotContents(slotIndex, itemstack);
		if (tile.getWorld() == null) {
			return;
		}
		if (slotIndex == SLOT_ANALYZE && !tile.getWorld().isRemote) {
			if (!AlleleManager.alleleRegistry.isIndividual(getStackInSlot(SLOT_ANALYZE)) && getStackInSlot(SLOT_ANALYZE) != null) {
				ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(getStackInSlot(SLOT_ANALYZE));
				if (ersatz != null) {
					setInventorySlotContents(SLOT_ANALYZE, ersatz);
				}
			}
			tile.getGame().initialize(getStackInSlot(SLOT_ANALYZE));
		}
	}
}
