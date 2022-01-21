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

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import forestry.api.core.tooltips.IToolTipProvider;
import forestry.api.core.tooltips.ToolTip;

public class SlotForestry extends Slot implements IToolTipProvider {

	private boolean isPhantom;
	private boolean canAdjustPhantom = true;
	private boolean canShift = true;
	private int stackLimit;
	@Nullable
	private ToolTip toolTips;

	public SlotForestry(Container inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
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
	public void set(ItemStack itemStack) {
		if (!isPhantom() || canAdjustPhantom()) {
			super.set(itemStack);
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
	public boolean mayPickup(Player stack) {
		return !isPhantom();
	}

	public boolean canShift() {
		return canShift;
	}

	@Override
	public int getMaxStackSize() {
		if (stackLimit < 0) {
			return super.getMaxStackSize();
		} else {
			return stackLimit;
		}
	}

	public void setToolTips(ToolTip toolTips) {
		this.toolTips = toolTips;
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		return toolTips;
	}

	@Override
	public boolean isToolTipVisible() {
		return getItem().isEmpty();
	}

	@Override
	public boolean isHovering(double mouseX, double mouseY) {
		return mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16;
	}
}
