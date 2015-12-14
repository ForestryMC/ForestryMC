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
package forestry.core.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.gui.slots.SlotWorking;
import forestry.core.inventory.InventoryAnalyzer;
import forestry.core.tiles.TileAnalyzer;

public class ContainerAnalyzer extends ContainerLiquidTanks<TileAnalyzer> {

	public ContainerAnalyzer(InventoryPlayer player, TileAnalyzer tile) {
		super(tile, player, 8, 94);

		// Input buffer
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 2; k++) {
				addSlotToContainer(new SlotFiltered(tile, InventoryAnalyzer.SLOT_INPUT_1 + i * 2 + k, 8 + k * 18, 28 + i * 18));
			}
		}

		// Analyze slot
		addSlotToContainer(new SlotWorking(tile, InventoryAnalyzer.SLOT_ANALYZE, 73, 59));

		// Can slot
		addSlotToContainer(new SlotFiltered(tile, InventoryAnalyzer.SLOT_CAN, 143, 24));

		// Output buffer
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 2; k++) {
				addSlotToContainer(new SlotOutput(tile, InventoryAnalyzer.SLOT_OUTPUT_1 + i * 2 + k, 134 + k * 18, 48 + i * 18));
			}
		}
	}
}
