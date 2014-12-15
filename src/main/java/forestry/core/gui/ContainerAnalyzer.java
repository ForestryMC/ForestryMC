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

import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.gadgets.TileAnalyzer;
import forestry.core.genetics.ItemGE;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.gui.slots.SlotLiquidContainer;
import forestry.core.gui.slots.SlotOutput;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAnalyzer extends ContainerLiquidTanks {

	private final TileAnalyzer tile;

	public ContainerAnalyzer(InventoryPlayer player, TileAnalyzer tile) {
		super(tile, tile);

		this.tile = tile;

		// Input buffer
		for (int i = 0; i < 3; i++)
			for (int k = 0; k < 2; k++)
				addSlot(new SlotCustom(tile, TileAnalyzer.SLOT_INPUT_1 + i * 2 + k, 8 + k * 18, 28 + i * 18, ItemGE.class));

		// Analyze slot
		addSlot(new SlotCustom(tile, TileAnalyzer.SLOT_ANALYZE, 73, 59));

		// Can slot
		addSlot(new SlotLiquidContainer(tile, TileAnalyzer.SLOT_CAN, 143, 24, false, Fluids.HONEY.get()) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				if (ForestryItem.honeyDrop.isItemEqual(stack))
					return true;
				return super.isItemValid(stack);
			}

		});

		// Output buffer
		for (int i = 0; i < 2; i++)
			for (int k = 0; k < 2; k++)
				addSlot(new SlotOutput(tile, TileAnalyzer.SLOT_OUTPUT_1 + i * 2 + k, 134 + k * 18, 48 + i * 18));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++)
			for (int l1 = 0; l1 < 9; l1++)
				addSlot(new Slot(player, l1 + i1 * 9 + 9, 8 + l1 * 18, 94 + i1 * 18));
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++)
			addSlot(new Slot(player, j1, 8 + j1 * 18, 152));

	}

}
