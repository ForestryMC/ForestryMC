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

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.render.ForestryResource;
import forestry.core.utils.SoundUtil;
import forestry.mail.POBoxInfo;

public class GuiMailboxInfo extends Gui {

	public enum XPosition {
		LEFT, RIGHT
	}

	public enum YPosition {
		TOP, BOTTOM
	}

	public static final GuiMailboxInfo instance = new GuiMailboxInfo();
	private static final int WIDTH = 98;
	private static final int HEIGHT = 17;

	private final FontRenderer fontRenderer;
	@Nullable
	private POBoxInfo poInfo;
	// TODO: this texture is a terrible waste of space in graphics memory, find a better way to do it.
	private final ResourceLocation textureAlert = new ForestryResource(Constants.TEXTURE_PATH_GUI + "/mailalert.png");

	private GuiMailboxInfo() {
		fontRenderer = Minecraft.getMinecraft().fontRenderer;
	}

	public void render() {
		if (poInfo == null || !Config.mailAlertEnabled || !poInfo.hasMail()) {
			return;
		}

		int x = 0;
		int y = 0;

		Minecraft minecraft = Minecraft.getMinecraft();
		ScaledResolution scaledresolution = new ScaledResolution(minecraft);
		if (Config.mailAlertXPosition == XPosition.RIGHT) {
			x = scaledresolution.getScaledWidth() - WIDTH;
		}
		if (Config.mailAlertYPosition == YPosition.BOTTOM) {
			y = scaledresolution.getScaledHeight() - HEIGHT;
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		TextureManager textureManager = minecraft.getTextureManager();
		textureManager.bindTexture(textureAlert);

		this.drawTexturedModalRect(x, y, 0, 0, WIDTH, HEIGHT);

		fontRenderer.drawString(Integer.toString(poInfo.playerLetters), x + 27 + getCenteredOffset(Integer.toString(poInfo.playerLetters), 22), y + 5, 0xffffff);
		fontRenderer.drawString(Integer.toString(poInfo.tradeLetters), x + 75 + getCenteredOffset(Integer.toString(poInfo.tradeLetters), 22), y + 5, 0xffffff);
	}

	protected int getCenteredOffset(String string, int xWidth) {
		return (xWidth - fontRenderer.getStringWidth(string)) / 2;
	}

	public boolean hasPOBoxInfo() {
		return poInfo != null;
	}

	@SideOnly(Side.CLIENT)
	public void setPOBoxInfo(EntityPlayer player, POBoxInfo info) {
		boolean playJingle = false;

		if (info.hasMail()) {
			if (this.poInfo == null) {
				playJingle = true;
			} else if (this.poInfo.playerLetters != info.playerLetters || this.poInfo.tradeLetters != info.tradeLetters) {
				playJingle = true;
			}
		}

		if (playJingle) {
			SoundUtil.playSoundEvent(SoundEvents.ENTITY_PLAYER_LEVELUP);
		}

		this.poInfo = info;
	}
}
