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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotWorking extends SlotForestry {

	public SlotWorking(IInventory iinventory, int i, int j, int k) {
		super(iinventory, i, j, k);
		blockShift();
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return false;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemStack) {
	}

	@Override
	public boolean canTakeStack(EntityPlayer stack) {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int i) {
		return null;
	}
}
