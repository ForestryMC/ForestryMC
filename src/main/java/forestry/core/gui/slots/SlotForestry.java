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
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;

public class SlotForestry extends Slot implements IToolTipProvider {

	protected boolean isPhantom;
	protected boolean isInfinite;
	protected boolean canAdjustPhantom = true;
	protected boolean canShift = true;
	protected int stackLimit;
	private ToolTip toolTips;

	public SlotForestry(IInventory inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
		if (inventory == null) {
			throw new IllegalArgumentException("Inventory must not be null");
		}
		this.stackLimit = -1;
	}

	public SlotForestry setInfinite() {
		this.isInfinite = true;
		return this;
	}

	public SlotForestry setPhantom() {
		isPhantom = true;
		return this;
	}

	public SlotForestry blockShift() {
		canShift = false;
		return this;
	}

	@Override
	public void putStack(ItemStack itemStack) {
		if (!isPhantom() || canAdjustPhantom()) {
			super.putStack(itemStack);
		}
	}

	public SlotForestry setCanAdjustPhantom(boolean canAdjust) {
		this.canAdjustPhantom = canAdjust;
		return this;
	}

	public SlotForestry setCanShift(boolean canShift) {
		this.canShift = canShift;
		return this;
	}

	public SlotForestry setStackLimit(int limit) {
		this.stackLimit = limit;
		return this;
	}

	public boolean isPhantom() {
		return this.isPhantom;
	}

	public boolean canAdjustPhantom() {
		return canAdjustPhantom;
	}

	@Override
	public boolean canTakeStack(EntityPlayer stack) {
		return !isPhantom();
	}

	public boolean canShift() {
		return canShift;
	}

	@Override
	public int getSlotStackLimit() {
		if (stackLimit < 0) {
			return super.getSlotStackLimit();
		} else {
			return stackLimit;
		}
	}

	@Override
	public ItemStack decrStackSize(int i) {
		if (!isInfinite) {
			return super.decrStackSize(i);
		}

		ItemStack stack = inventory.getStackInSlot(getSlotIndex());
		if (stack == null) {
			return null;
		}

		ItemStack result = stack.copy();
		result.stackSize = i;
		return result;
	}

	/**
	 * @param toolTips the tooltips to set
	 */
	public void setToolTips(ToolTip toolTips) {
		this.toolTips = toolTips;
	}

	/**
	 * @return the toolTips
	 */
	@Override
	public ToolTip getToolTip() {
		return toolTips;
	}

	@Override
	public boolean isToolTipVisible() {
		return getStack() == null;
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= xDisplayPosition && mouseX <= xDisplayPosition + 16 && mouseY >= yDisplayPosition && mouseY <= yDisplayPosition + 16;
	}
}
