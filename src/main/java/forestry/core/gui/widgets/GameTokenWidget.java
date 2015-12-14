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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import forestry.core.gui.GuiUtil;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.render.TextureManager;
import forestry.core.tiles.EscritoireGame;
import forestry.core.tiles.EscritoireGameToken;

public class GameTokenWidget extends Widget {
	private final ItemStack HIDDEN_TOKEN = new ItemStack(Items.book);

	private final EscritoireGame game;
	private final int index;

	public GameTokenWidget(EscritoireGame game, WidgetManager manager, int xPos, int yPos, int index) {
		super(manager, xPos, yPos);
		this.game = game;
		this.index = index;
	}

	private EscritoireGameToken getToken() {
		return game.getToken(index);
	}

	@Override
	public void draw(int startX, int startY) {

		EscritoireGameToken token = getToken();
		if (token == null) {
			return;
		}

		int tokenColour = token.getTokenColour();

		float colorR = (tokenColour >> 16 & 255) / 255.0F;
		float colorG = (tokenColour >> 8 & 255) / 255.0F;
		float colorB = (tokenColour & 255) / 255.0F;

		Proxies.render.bindTexture(manager.gui.textureFile);

		GL11.glColor4f(colorR, colorG, colorB, 1.0F);
		manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, 228, 0, 22, 22);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);

		ItemStack tokenStack = HIDDEN_TOKEN;
		if (token.isVisible()) {
			tokenStack = token.getTokenStack();
		}

		GuiUtil.drawItemStack(manager.gui, tokenStack, startX + xPos + 3, startY + yPos + 3);

		manager.gui.setZLevel(150f);
		for (String ident : getToken().getOverlayIcons()) {
			RenderHelper.enableGUIStandardItemLighting();
			Proxies.render.bindTexture(SpriteSheet.ITEMS);
			IIcon icon = TextureManager.getInstance().getDefault(ident);
			manager.gui.drawTexturedModelRectFromIcon(startX + xPos + 3, startY + yPos + 3, icon, 16, 16);
			RenderHelper.disableStandardItemLighting();
		}
		manager.gui.setZLevel(0f);
	}

	@Override
	protected String getLegacyTooltip(EntityPlayer player) {
		EscritoireGameToken token = getToken();
		if (token == null || !token.isVisible()) {
			return null;
		}

		return token.getTooltip();
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		Proxies.net.sendToServer(new PacketGuiSelectRequest(index, 0));
	}
}
