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
package forestry.core.gui.slots;

import net.minecraft.inventory.IInventory;

import forestry.core.gui.IContainerCrafting;

/**
 * Informs the passed container of slot changes. Contains a dummy itemstack.
 */
public class SlotCraftMatrix extends SlotForestry {

	private final IContainerCrafting eventHandler;
	private final int slot;

	public SlotCraftMatrix(IContainerCrafting container, IInventory iinventory, int i, int j, int k) {
		super(iinventory, i, j, k);
		setPhantom();
		this.eventHandler = container;
		this.slot = i;
		setStackLimit(1);
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		eventHandler.onCraftMatrixChanged(inventory, slot);
	}

}
