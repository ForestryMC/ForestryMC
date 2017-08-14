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
package forestry.factory.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotEmptyLiquidContainerIn;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotOutput;
import forestry.factory.inventory.InventoryBottler;
import forestry.factory.tiles.TileBottler;

public class ContainerBottler extends ContainerLiquidTanks<TileBottler> {

	public ContainerBottler(InventoryPlayer player, TileBottler tile) {
		super(tile, player, 8, 84);

		this.addSlotToContainer(new SlotLiquidIn(tile, InventoryBottler.SLOT_INPUT_FULL_CONTAINER, 18, 7));
		this.addSlotToContainer(new SlotOutput(tile, InventoryBottler.SLOT_EMPTYING_PROCESSING, 18, 35).setPickupWatcher(tile));
		this.addSlotToContainer(new SlotOutput(tile, InventoryBottler.SLOT_OUTPUT_EMPTY_CONTAINER, 18, 63));
		this.addSlotToContainer(new SlotEmptyLiquidContainerIn(tile, InventoryBottler.SLOT_INPUT_EMPTY_CONTAINER, 142, 7));
		this.addSlotToContainer(new SlotOutput(tile, InventoryBottler.SLOT_FILLING_PROCESSING, 142, 35).setPickupWatcher(tile));
		this.addSlotToContainer(new SlotOutput(tile, InventoryBottler.SLOT_OUTPUT_FULL_CONTAINER, 142, 63));
	}
}
