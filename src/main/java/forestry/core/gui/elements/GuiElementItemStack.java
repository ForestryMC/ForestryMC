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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.gui.GuiUtil;

public class GuiElementItemStack extends GuiElement {
	private final ItemStack itemStack;

	public GuiElementItemStack(int xPos, int yPos, ItemStack itemStack) {
		super(xPos, yPos, 16, 16);
		this.itemStack = itemStack;
	}

	@Override
	public void draw(int startX, int startY) {
		if (!itemStack.isEmpty()) {
			GuiUtil.drawItemStack(Minecraft.getMinecraft().fontRenderer, itemStack, getX() + startX, getY() + startY);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public List<String> getToolTip(int mouseX, int mouseY) {
		Minecraft minecraft = Minecraft.getMinecraft();
		EntityPlayer player = minecraft.player;
		List<String> tip = new ArrayList<>();
		if (!itemStack.isEmpty()) {
			tip.addAll(itemStack.getTooltip(player, minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
		}
		return tip;
	}
}
