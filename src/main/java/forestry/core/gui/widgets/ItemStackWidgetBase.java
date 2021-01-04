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

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.tooltips.ToolTip;
import forestry.core.gui.GuiUtil;
import forestry.core.utils.ItemTooltipUtil;

public abstract class ItemStackWidgetBase extends Widget {
    public ItemStackWidgetBase(WidgetManager widgetManager, int xPos, int yPos) {
        super(widgetManager, xPos, yPos);
    }

    protected abstract ItemStack getItemStack();

    @Override
    public void draw(MatrixStack transform, int startY, int startX) {
        ItemStack itemStack = getItemStack();
        if (!itemStack.isEmpty()) {
            //RenderHelper.enableGUIStandardItemLighting(); TODO Gui Light
            GuiUtil.drawItemStack(manager.gui, itemStack, xPos + startX, yPos + startY);
            RenderHelper.disableStandardItemLighting();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ToolTip getToolTip(int mouseX, int mouseY) {
        ItemStack itemStack = getItemStack();
        ToolTip tip = new ToolTip();
        if (!itemStack.isEmpty()) {
            tip.addAll(ItemTooltipUtil.getInformation(itemStack));
        }
        return tip;
    }
}
