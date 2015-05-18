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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.gui.slots.SlotLocked;
import forestry.core.inventory.ItemInventory;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;

public abstract class ContainerItemInventory<I extends ItemInventory> extends ContainerForestry {

	protected final I inventory;

	public ContainerItemInventory(I inventory) {
		super(inventory);
		this.inventory = inventory;
	}

	protected void addSecuredSlot(InventoryPlayer playerInventory, int slot, int x, int y) {
		ItemStack stackInSlot = playerInventory.getStackInSlot(slot);
		if (StackUtils.isIdenticalItem(inventory.parent, stackInSlot)) {
			addSlotToContainer(new SlotLocked(playerInventory, slot, x, y));
		} else {
			addSlotToContainer(new Slot(playerInventory, slot, x, y));
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if (!Proxies.common.isSimulating(player.worldObj)) {
			return;
		}

		inventory.onGuiSaved(player);
	}
}
