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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.fml.loading.FMLEnvironment;

import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.render.TextureManagerForestry;

public class SlotForestry extends Slot implements IToolTipProvider {

	private boolean isPhantom;
	private boolean canAdjustPhantom = true;
	private boolean canShift = true;
	private int stackLimit;
	@Nullable
	private ToolTip toolTips;

	public SlotForestry(IInventory inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
		if (FMLEnvironment.dist == Dist.CLIENT) {
			setBackgroundLocation(TextureManagerForestry.getInstance().getGuiTextureMap());
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
	public boolean canTakeStack(PlayerEntity stack) {
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

	public void setToolTips(ToolTip toolTips) {
		this.toolTips = toolTips;
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		return toolTips;
	}

	@Override
	public boolean isToolTipVisible() {
		return getStack().isEmpty();
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= xPos && mouseX <= xPos + 16 && mouseY >= yPos && mouseY <= yPos + 16;
	}
}
