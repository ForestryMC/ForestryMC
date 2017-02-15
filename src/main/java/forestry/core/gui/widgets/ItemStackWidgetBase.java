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

import forestry.core.gui.GuiUtil;
import forestry.core.gui.tooltips.ToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemStackWidgetBase extends Widget {
	public ItemStackWidgetBase(WidgetManager widgetManager, int xPos, int yPos) {
		super(widgetManager, xPos, yPos);
	}

	protected abstract ItemStack getItemStack();

	@Override
	public void draw(int startX, int startY) {
		ItemStack itemStack = getItemStack();
		if (!itemStack.isEmpty()) {
			GuiUtil.drawItemStack(manager.gui, itemStack, xPos + startX, yPos + startY);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		Minecraft minecraft = Minecraft.getMinecraft();
		EntityPlayer player = minecraft.player;
		ItemStack itemStack = getItemStack();
		ToolTip tip = new ToolTip();
		if (!itemStack.isEmpty()) {
			tip.add(itemStack.getTooltip(player, minecraft.gameSettings.advancedItemTooltips));
		}
		return tip;
	}
}
