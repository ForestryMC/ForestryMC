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
package forestry.mail.gui.widgets;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.PostManager;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.utils.StringUtil;
import forestry.mail.gui.ContainerLetter;

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
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
			Proxies.render.bindTexture(SpriteSheet.ITEMS);
			manager.gui.drawTexturedModelRectFromIcon(startX + xPos, startY + yPos - 5, carrier.getIcon(), 26, 26);
		}
	}

	@Override
	protected String getLegacyTooltip(EntityPlayer player) {
		return StringUtil.localize("gui.addressee." + containerLetter.getCarrierType().toString());
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if (!containerLetter.getLetter().isProcessed()) {
			containerLetter.advanceCarrierType();
		}
	}
}
