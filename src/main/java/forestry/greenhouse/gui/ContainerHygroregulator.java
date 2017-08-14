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
package forestry.greenhouse.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.greenhouse.inventory.InventoryHygroregulator;
import forestry.greenhouse.tiles.TileHygroregulator;

public class ContainerHygroregulator extends ContainerLiquidTanks<TileHygroregulator> {

	public ContainerHygroregulator(InventoryPlayer playerInventory, TileHygroregulator tile) {
		super(tile, playerInventory, 8, 84);

		this.addSlotToContainer(new SlotLiquidIn(tile, InventoryHygroregulator.SLOT_INPUT, 56, 38));
	}

}
