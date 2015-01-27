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
package forestry.apiculture.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import forestry.apiculture.gadgets.TileAlvearyHygroregulator;
import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotFiltered;

public class ContainerAlvearyHygroregulator extends ContainerLiquidTanks {

	public ContainerAlvearyHygroregulator(IInventory playerInventory, TileAlvearyHygroregulator tile) {
		super(tile);

		this.addSlotToContainer(new SlotFiltered(tile, TileAlvearyHygroregulator.SLOT_INPUT, 56, 38));

		for (int i = 0; i < 3; ++i) {
			for (int var4 = 0; var4 < 9; ++var4) {
				this.addSlotToContainer(new Slot(playerInventory, var4 + i * 9 + 9, 8 + var4 * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
		}

	}

}
