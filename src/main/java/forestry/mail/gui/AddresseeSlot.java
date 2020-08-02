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
package forestry.mail.gui;

import net.minecraft.client.gui.AbstractGui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import forestry.api.core.tooltips.ToolTip;
import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.PostManager;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.SoundUtil;

public class AddresseeSlot extends Widget {

    private final ContainerLetter containerLetter;

    public AddresseeSlot(WidgetManager widgetManager, int xPos, int yPos, ContainerLetter containerLetter) {
        super(widgetManager, xPos, yPos);
        this.containerLetter = containerLetter;
        this.width = 26;
        this.height = 15;
    }

    @Override
    public void draw(MatrixStack transform, int startY, int startX) {
        IPostalCarrier carrier = PostManager.postRegistry.getCarrier(containerLetter.getCarrierType());
        if (carrier != null) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0F);
            TextureManagerForestry.getInstance().bindGuiTextureMap();
            AbstractGui.blit(transform, startX + xPos, startY + yPos, manager.gui.getBlitOffset(), 32, 32, carrier.getSprite());
        }
    }

    @Override
    public ToolTip getToolTip(int mouseX, int mouseY) {
        ToolTip tooltip = new ToolTip();
        tooltip.translated("for.gui.addressee." + containerLetter.getCarrierType());
        return tooltip;
    }

    @Override
    public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
        if (!containerLetter.getLetter().isProcessed()) {
            containerLetter.advanceCarrierType();
            SoundUtil.playButtonClick();
        }
    }
}
