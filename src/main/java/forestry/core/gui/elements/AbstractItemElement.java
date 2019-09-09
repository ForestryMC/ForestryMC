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

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.GuiUtil;
import forestry.core.utils.ItemTooltipUtil;

public abstract class AbstractItemElement extends GuiElement {

	public AbstractItemElement(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
	}

	public AbstractItemElement(int xPos, int yPos) {
		super(xPos, yPos, 16, 16);
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		ItemStack itemStack = getStack();
		if (!itemStack.isEmpty()) {
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.enableRescaleNormal();
			GuiUtil.drawItemStack(Minecraft.getInstance().fontRenderer, itemStack, 0, 0);
			RenderHelper.disableStandardItemLighting();
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public List<ITextComponent> getTooltip(int mouseX, int mouseY) {
		ItemStack itemStack = getStack();
		return ItemTooltipUtil.getInformation(itemStack);
	}

	@Override
	public boolean hasTooltip() {
		return true;
	}

	protected abstract ItemStack getStack();
}
