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

import forestry.apiculture.gadgets.TileAlvearyHygroregulator;
import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotLiquidContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerAlvearyHygroregulator extends ContainerLiquidTanks {

	private final TileAlvearyHygroregulator tile;

	public ContainerAlvearyHygroregulator(IInventory inventory, TileAlvearyHygroregulator tile) {
		super(inventory, tile);

		this.tile = tile;
		this.addSlot(new SlotLiquidContainer(tile, 0, 56, 38));

		for (int i = 0; i < 3; ++i)
			for (int var4 = 0; var4 < 9; ++var4)
				this.addSlot(new Slot(inventory, var4 + i * 9 + 9, 8 + var4 * 18, 84 + i * 18));

		for (int i = 0; i < 9; ++i)
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));

	}

}
