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
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.render.TextureManager;
import forestry.core.tiles.IFilterSlotDelegate;

/**
 * Slot which only takes specific items, specified by the IFilterSlotDelegate.
 */
public class SlotFiltered extends SlotWatched {
	private String blockedTexture = "slots/blocked";
	private final IFilterSlotDelegate filterSlotDelegate;

	public <T extends IInventory & IFilterSlotDelegate> SlotFiltered(T inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
		this.filterSlotDelegate = inventory;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		int slotIndex = getSlotIndex();
		if (filterSlotDelegate.isLocked(slotIndex)) {
			return false;
		}
		if (itemstack != null) {
			return filterSlotDelegate.canSlotAccept(slotIndex, itemstack);
		}
		return true;
	}

	public SlotFiltered setBlockedTexture(String ident) {
		blockedTexture = ident;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getBackgroundIconIndex() {
		ItemStack stack = getStack();
		if (!isItemValid(stack)) {
			return TextureManager.getInstance().getDefault(blockedTexture);
		} else {
			return null;
		}
	}
}
