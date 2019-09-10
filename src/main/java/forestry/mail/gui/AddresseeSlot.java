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

import com.mojang.blaze3d.platform.GlStateManager;

import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.PostManager;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.SoundUtil;
import forestry.core.utils.Translator;

public class AddresseeSlot extends Widget {

	private final ContainerLetter containerLetter;

	public AddresseeSlot(WidgetManager widgetManager, int xPos, int yPos, ContainerLetter containerLetter) {
		super(widgetManager, xPos, yPos);
		this.containerLetter = containerLetter;
		this.width = 26;
		this.height = 15;
	}

	@Override
	public void draw(int startX, int startY) {
		IPostalCarrier carrier = PostManager.postRegistry.getCarrier(containerLetter.getCarrierType());
		if (carrier != null) {
			GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0F);
			TextureManagerForestry.getInstance().bindGuiTextureMap();
			AbstractGui.blit(startX + xPos, startY + yPos, manager.gui.getBlitOffset(), 32, 32, carrier.getSprite());
		}
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		String tooltipString = Translator.translateToLocal("for.gui.addressee." + containerLetter.getCarrierType());
		ToolTip tooltip = new ToolTip();
		tooltip.add(tooltipString);
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
