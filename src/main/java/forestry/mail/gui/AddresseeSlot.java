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

import net.minecraft.client.renderer.GlStateManager;

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
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0F);
			TextureManagerForestry.getInstance().bindGuiTextureMap();
			manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, carrier.getSprite(), 32, 32);
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
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if (!containerLetter.getLetter().isProcessed()) {
			containerLetter.advanceCarrierType();
			SoundUtil.playButtonClick();
		}
	}
}
