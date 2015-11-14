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

	private boolean isPhantom;
	private boolean canAdjustPhantom = true;
	private boolean canShift = true;
	private int stackLimit;
	private ToolTip toolTips;

	public SlotForestry(IInventory inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
		if (inventory == null) {
			throw new IllegalArgumentException("Inventory must not be null");
		}
		this.stackLimit = -1;
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

	/**
	 * @param toolTips the tooltips to set
	 */
	public void setToolTips(ToolTip toolTips) {
		this.toolTips = toolTips;
	}

	/**
	 * @return the toolTips
	 * @param mouseX
	 * @param mouseY
	 */
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
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
