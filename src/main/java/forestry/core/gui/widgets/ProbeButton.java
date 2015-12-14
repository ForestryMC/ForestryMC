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

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import forestry.core.gui.GuiEscritoire;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class ProbeButton extends Widget {

	private final GuiEscritoire guiEscritoire;
	private boolean pressed;

	public ProbeButton(GuiEscritoire guiEscritoire, WidgetManager manager, int xPos, int yPos) {
		super(manager, xPos, yPos);
		this.guiEscritoire = guiEscritoire;
		width = 22;
		height = 25;
	}

	@Override
	public void draw(int startX, int startY) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
		Proxies.render.bindTexture(manager.gui.textureFile);
		manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, 228, pressed ? 47 : 22, width, height);
	}

	@Override
	protected String getLegacyTooltip(EntityPlayer player) {
		return StringUtil.localize("gui.escritoire.probe");
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		pressed = true;
		Proxies.net.sendToServer(new PacketGuiSelectRequest(-1, 0));
	}

	@Override
	public void handleMouseRelease(int mouseX, int mouseY, int eventType) {
		if (pressed) {
			pressed = false;
		}
	}

	@Override
	public void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
		if (manager.getAtPosition(mouseX - guiEscritoire.getGuiLeft(), mouseY - guiEscritoire.getGuiTop()) != this) {
			pressed = false;
		}
	}
}
