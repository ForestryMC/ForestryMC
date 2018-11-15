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

public class SlotLockable extends SlotForestry {

	private boolean locked;

	public SlotLockable(IInventory inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
	}

	public void lock() {
		this.locked = true;
		setCanAdjustPhantom(false);
		blockShift();
		setPhantom();
	}

	@Override
	public ItemStack onTake(EntityPlayer player, ItemStack itemStack) {
		if (!locked) {
			return super.onTake(player, itemStack);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return !locked && super.isItemValid(par1ItemStack);
	}

	@Override
	public ItemStack decrStackSize(int i) {
		if (!locked) {
			return super.decrStackSize(i);
		}
		return ItemStack.EMPTY;
	}
}
