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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.render.ForestryResource;
import forestry.core.utils.SoundUtil;
import forestry.mail.POBoxInfo;

public class GuiMailboxInfo extends GuiComponent {

	public enum XPosition {
		LEFT, RIGHT
	}

	public enum YPosition {
		TOP, BOTTOM
	}

	public static final GuiMailboxInfo instance = new GuiMailboxInfo();
	private static final int WIDTH = 98;
	private static final int HEIGHT = 17;

	private final Font fontRenderer;
	@Nullable
	private POBoxInfo poInfo;
	// TODO: this texture is a terrible waste of space in graphics memory, find a better way to do it.
	private final ResourceLocation textureAlert = new ForestryResource(Constants.TEXTURE_PATH_GUI + "/mailalert.png");

	private GuiMailboxInfo() {
		fontRenderer = Minecraft.getInstance().font;
	}

	public void render(PoseStack transform) {
		if (poInfo == null || !Config.mailAlertEnabled || !poInfo.hasMail()) {
			return;
		}

		int x = 0;
		int y = 0;

		Minecraft minecraft = Minecraft.getInstance();
		Window win = minecraft.getWindow();
		if (Config.mailAlertXPosition == XPosition.RIGHT) {
			x = win.getGuiScaledWidth() - WIDTH;
		}
		if (Config.mailAlertYPosition == YPosition.BOTTOM) {
			y = win.getGuiScaledHeight() - HEIGHT;
		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		// RenderSystem.disableLighting();
		RenderSystem.setShaderTexture(0, textureAlert);

		this.blit(transform, x, y, 0, 0, WIDTH, HEIGHT);

		fontRenderer.draw(transform, Integer.toString(poInfo.playerLetters), x + 27 + getCenteredOffset(Integer.toString(poInfo.playerLetters), 22), y + 5, 0xffffff);
		fontRenderer.draw(transform, Integer.toString(poInfo.tradeLetters), x + 75 + getCenteredOffset(Integer.toString(poInfo.tradeLetters), 22), y + 5, 0xffffff);
	}

	protected int getCenteredOffset(String string, int xWidth) {
		return (xWidth - fontRenderer.width(string)) / 2;
	}

	public boolean hasPOBoxInfo() {
		return poInfo != null;
	}

	@OnlyIn(Dist.CLIENT)
	public void setPOBoxInfo(Player player, POBoxInfo info) {
		boolean playJingle = false;

		if (info.hasMail()) {
			if (this.poInfo == null) {
				playJingle = true;
			} else if (this.poInfo.playerLetters != info.playerLetters || this.poInfo.tradeLetters != info.tradeLetters) {
				playJingle = true;
			}
		}

		if (playJingle) {
			SoundUtil.playSoundEvent(SoundEvents.PLAYER_LEVELUP);
		}

		this.poInfo = info;
	}
}
