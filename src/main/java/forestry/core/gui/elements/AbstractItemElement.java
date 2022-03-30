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
package forestry.core.gui.elements;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.tooltips.ToolTip;
import forestry.core.gui.GuiUtil;
import forestry.core.utils.ItemTooltipUtil;

public abstract class AbstractItemElement extends GuiElement {

	public AbstractItemElement(int xPos, int yPos) {
		super(xPos, yPos);
		setSize(16, 16);
	}

	@Override
	public void drawElement(PoseStack transform, int mouseX, int mouseY) {
		ItemStack itemStack = getStack();
		if (!itemStack.isEmpty()) {
			//RenderHelper.enableGUIStandardItemLighting(); TODO Gui Light
			// GlStateManager._enableRescaleNormal();
			GuiUtil.drawItemStack(Minecraft.getInstance().font, itemStack, 0, 0);
			// Lighting.turnOff();
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ToolTip getTooltip(int mouseX, int mouseY) {
		ItemStack itemStack = getStack();
		return ItemTooltipUtil.getInformation(itemStack);
	}

	@Override
	public boolean hasTooltip() {
		return true;
	}

	protected abstract ItemStack getStack();
}
