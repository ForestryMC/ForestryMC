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
package forestry.core.gui.widgets;

import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import org.lwjgl.opengl.GL11;

import forestry.api.core.IToolPipette;
import forestry.api.core.sprite.ISprite;
import forestry.api.core.sprite.Sprite;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.gui.IContainerLiquidTanks;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.proxy.Proxies;
import forestry.farming.gui.ContainerFarm;

/**
 * Slot for liquid tanks
 */
public class TankWidget extends Widget {

	private int overlayTexX = 176;
	private int overlayTexY = 0;
	private int slot = 0;

	public TankWidget(WidgetManager manager, int xPos, int yPos, int slot) {
		super(manager, xPos, yPos);
		this.slot = slot;
		this.height = 58;
	}

	public TankWidget setOverlayOrigin(int x, int y) {
		overlayTexX = x;
		overlayTexY = y;
		return this;
	}

	public IFluidTank getTank() {
		Container container = manager.gui.inventorySlots;
		if (container instanceof IContainerLiquidTanks) {
			return ((IContainerLiquidTanks) container).getTank(slot);
		} else if (container instanceof ContainerFarm) {
			return ((ContainerFarm) container).getTank(slot);
		}
		return null;
	}

	@Override
	public void draw(int startX, int startY) {
		IFluidTank tank = getTank();
		if (tank == null) {
			return;
		}

		FluidStack contents = tank.getFluid();
		if (contents == null || contents.amount <= 0 || contents.getFluid() == null) {
			return;
		}
		if(contents.getFluid().getStill() == null)
			return;
		
		int squaled = (contents.amount * height) / getTank().getCapacity();

		Proxies.common.bindTexture(contents.getFluid().getStill());
		int start = 0;

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
		while (true) {
			int x;

			if (squaled > 16) {
				x = 16;
				squaled -= 16;
			} else {
				x = squaled;
				squaled = 0;
			}

			manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos + height - x - start, 0, 0, 16, 16 - (16 - x));
			start = start + 16;

			if (x == 0 || squaled == 0) {
				break;
			}
		}

		Proxies.common.bindTexture(manager.gui.textureFile);
		manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, overlayTexX, overlayTexY, 16, 60);
		GL11.glPopAttrib();
	}

	@Override
	public ToolTip getToolTip() {
		IFluidTank tank = getTank();
		if (!(tank instanceof StandardTank)) {
			return null;
		}
		return ((StandardTank) tank).getToolTip();
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		ItemStack itemstack = manager.minecraft.thePlayer.inventory.getItemStack();
		if (itemstack == null) {
			return;
		}

		Item held = itemstack.getItem();
		if (held instanceof IToolPipette && manager.gui.inventorySlots instanceof IContainerLiquidTanks) {
			((IContainerLiquidTanks) manager.gui.inventorySlots).handlePipetteClickClient(slot, manager.minecraft.thePlayer);
		}
	}
}
