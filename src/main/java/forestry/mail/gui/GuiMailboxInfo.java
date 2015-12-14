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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.render.ForestryResource;
import forestry.mail.POBoxInfo;

public class GuiMailboxInfo extends Gui {

	public enum XPosition {
		LEFT, RIGHT;
	}

	public enum YPosition {
		TOP, BOTTOM;
	}

	public static final GuiMailboxInfo instance = new GuiMailboxInfo();
	private static final int WIDTH = 98;
	private static final int HEIGHT = 17;

	private final FontRenderer fontRendererObj;
	private POBoxInfo poInfo;
	private final ResourceLocation textureAlert = new ForestryResource(Constants.TEXTURE_PATH_GUI + "/mailalert.png");

	private GuiMailboxInfo() {
		fontRendererObj = Proxies.common.getClientInstance().fontRenderer;
	}

	public void render() {
		if (poInfo == null || !Config.mailAlertEnabled || !poInfo.hasMail()) {
			return;
		}

		int x = 0;
		int y = 0;

		Minecraft minecraft = Minecraft.getMinecraft();
		ScaledResolution scaledresolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
		if (Config.mailAlertXPosition == XPosition.RIGHT) {
			x = scaledresolution.getScaledWidth() - WIDTH;
		}
		if (Config.mailAlertYPosition == YPosition.BOTTOM) {
			y = scaledresolution.getScaledHeight() - HEIGHT;
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Proxies.render.bindTexture(textureAlert);

		this.drawTexturedModalRect(x, y, 0, 0, WIDTH, HEIGHT);

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
