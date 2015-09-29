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

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.render.ForestryResource;
import forestry.mail.POBoxInfo;

public class GuiMailboxInfo extends Gui {

	public final static GuiMailboxInfo instance = new GuiMailboxInfo();

	private final FontRenderer fontRendererObj;
	private POBoxInfo poInfo;
	private final ResourceLocation textureAlert = new ForestryResource(Constants.TEXTURE_PATH_GUI + "/mailalert.png");

	private GuiMailboxInfo() {
		fontRendererObj = Proxies.common.getClientInstance().fontRenderer;
	}

	public void render(int x, int y) {
		if (poInfo == null) {
			return;
		}
		if (Proxies.common.getRenderWorld() == null) {
			return;
		}
		if (!Config.mailAlertEnabled) {
			return;
		}
		if (!poInfo.hasMail()) {
			return;
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Proxies.common.bindTexture(textureAlert);

		this.drawTexturedModalRect(x, y, 0, 0, 98, 17);

		fontRendererObj.drawString(Integer.toString(poInfo.playerLetters), x + 27 + getCenteredOffset(Integer.toString(poInfo.playerLetters), 22), y + 5, 0xffffff);
		fontRendererObj.drawString(Integer.toString(poInfo.tradeLetters), x + 75 + getCenteredOffset(Integer.toString(poInfo.tradeLetters), 22), y + 5, 0xffffff);
	}

	protected int getCenteredOffset(String string, int xWidth) {
		return (xWidth - fontRendererObj.getStringWidth(string)) / 2;
	}

	public boolean hasPOBoxInfo() {
		return poInfo != null;
	}

	public void setPOBoxInfo(POBoxInfo info) {
		boolean playJingle = false;

		if (info.hasMail()) {
			if (this.poInfo == null) {
				playJingle = true;
			} else if (this.poInfo.playerLetters != info.playerLetters || this.poInfo.tradeLetters != info.tradeLetters) {
				playJingle = true;
			}
		}

		if (playJingle) {
			Proxies.common.getRenderWorld().playSoundAtEntity(Proxies.common.getClientInstance().thePlayer, "random.levelup", 1.0f, 1.0f);
		}

		this.poInfo = info;
	}
}
