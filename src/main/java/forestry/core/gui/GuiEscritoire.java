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
package forestry.core.gui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.List;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Defaults;
import forestry.core.gadgets.NaturalistGame;
import forestry.core.gadgets.NaturalistGame.GameToken;
import forestry.core.gadgets.TileEscritoire;
import forestry.core.gui.widgets.Widget;
import forestry.core.network.PacketGuiSelect;
import forestry.core.network.PacketId;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class GuiEscritoire extends GuiForestry<ContainerEscritoire, TileEscritoire> {

	private static enum Notes {
		level1, level2, level3, level4, success, failure
	}

	private static final ListMultimap<Notes, String> researchNotes;

	static {
		researchNotes = ArrayListMultimap.create();
		for (Notes notesLevel : Notes.values()) {
			int levelCount = Integer.valueOf(StringUtil.localize("gui.escritoire.notes." + notesLevel + ".count"));
			for (int i = 1; i <= levelCount; i++) {
				String note = StringUtil.localize("gui.escritoire.notes." + notesLevel + "." + i);
				researchNotes.put(notesLevel, note);
			}
		}
	}

	private class TokenSlot extends Widget {

		private final ItemStack HIDDEN_TOKEN;
		private final int index;

		public TokenSlot(WidgetManager manager, int xPos, int yPos, int index) {
			super(manager, xPos, yPos);
			this.index = index;

			HIDDEN_TOKEN = new ItemStack(Items.book);
		}

		private boolean hasToken() {
			return inventory.getGame().getToken(index) != null;
		}

		private GameToken getToken() {
			return inventory.getGame().getToken(index);
		}

		@Override
		public void draw(int startX, int startY) {

			if (!hasToken()) {
				return;
			}

			int tokenColour = getToken().getTokenColour();

			float colorR = (tokenColour >> 16 & 255) / 255.0F;
			float colorG = (tokenColour >> 8 & 255) / 255.0F;
			float colorB = (tokenColour & 255) / 255.0F;

			manager.gui.bindTexture(manager.gui.textureFile);
			GL11.glColor4f(colorR, colorG, colorB, 1.0F);
			manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, 228, 0, 22, 22);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);

			ItemStack tokenStack = HIDDEN_TOKEN;
			if (getToken().isVisible()) {
				tokenStack = getToken().tokenStack;
			}


			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableGUIStandardItemLighting();
			manager.gui.drawItemStack(tokenStack, startX + xPos + 3, startY + yPos + 3);
			RenderHelper.disableStandardItemLighting();
			GL11.glPopAttrib();

			manager.gui.setZLevel(150f);
			for (String ident : getToken().getOverlayIcons()) {
				RenderHelper.enableGUIStandardItemLighting();
				Proxies.common.bindTexture(SpriteSheet.ITEMS);
				manager.gui.drawTexturedModelRectFromIcon(startX + xPos + 3, startY + yPos + 3, TextureManager.getInstance().getDefault(ident), 16, 16);
				RenderHelper.disableStandardItemLighting();
			}
			manager.gui.setZLevel(0f);
		}

		@Override
		protected String getLegacyTooltip(EntityPlayer player) {
			return hasToken() && getToken().isVisible() ? getToken().getTooltip() : null;
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			PacketGuiSelect packet = new PacketGuiSelect(PacketId.GUI_SELECTION_CHANGE, index, 0);
			Proxies.net.sendToServer(packet);
		}
	}

	private class ProbeButton extends Widget {

		private boolean pressed;

		public ProbeButton(WidgetManager manager, int xPos, int yPos) {
			super(manager, xPos, yPos);
			width = 22;
			height = 25;
		}

		@Override
		public void draw(int startX, int startY) {
			manager.gui.bindTexture(manager.gui.textureFile);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
			manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, 228, pressed ? 47 : 22, width, height);
		}

		@Override
		protected String getLegacyTooltip(EntityPlayer player) {
			return StringUtil.localize("gui.escritoire.probe");
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			pressed = true;
			PacketGuiSelect packet = new PacketGuiSelect(PacketId.GUI_SELECTION_CHANGE, -1, 0);
			Proxies.net.sendToServer(packet);
		}

		@Override
		public void handleMouseRelease(int mouseX, int mouseY, int eventType) {
			if (pressed) {
				pressed = false;
			}
		}

		@Override
		public void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
			if (widgetManager.getAtPosition(mouseX - getGuiLeft(), mouseY - getGuiTop()) != this) {
				pressed = false;
			}
		}
	}

	private final ItemStack LEVEL_ITEM;
	private String researchNote = "";
	private long lastUpdate;

	public GuiEscritoire(EntityPlayer player, TileEscritoire tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/escritoire.png", new ContainerEscritoire(player, tile), tile);

		LEVEL_ITEM = new ItemStack(Items.paper);

		xSize = 228;
		ySize = 235;

		widgetManager.add(new ProbeButton(widgetManager, 14, 16));

		// Inner ring
		widgetManager.add(new TokenSlot(widgetManager, 115, 51, 0));
		widgetManager.add(new TokenSlot(widgetManager, 115, 77, 1));
		widgetManager.add(new TokenSlot(widgetManager, 94, 90, 2));
		widgetManager.add(new TokenSlot(widgetManager, 73, 77, 3));
		widgetManager.add(new TokenSlot(widgetManager, 73, 51, 4));
		widgetManager.add(new TokenSlot(widgetManager, 94, 38, 5));

		// Outer ring
		widgetManager.add(new TokenSlot(widgetManager, 115, 25, 6));
		widgetManager.add(new TokenSlot(widgetManager, 136, 38, 7));
		widgetManager.add(new TokenSlot(widgetManager, 136, 64, 8));

		widgetManager.add(new TokenSlot(widgetManager, 136, 90, 9));
		widgetManager.add(new TokenSlot(widgetManager, 115, 103, 10));
		widgetManager.add(new TokenSlot(widgetManager, 94, 116, 11));

		widgetManager.add(new TokenSlot(widgetManager, 73, 103, 12));
		widgetManager.add(new TokenSlot(widgetManager, 52, 90, 13));
		widgetManager.add(new TokenSlot(widgetManager, 52, 64, 14));

		widgetManager.add(new TokenSlot(widgetManager, 52, 38, 15));
		widgetManager.add(new TokenSlot(widgetManager, 73, 25, 16));
		widgetManager.add(new TokenSlot(widgetManager, 94, 12, 17));

		// Corners
		widgetManager.add(new TokenSlot(widgetManager, 52, 12, 18));
		widgetManager.add(new TokenSlot(widgetManager, 136, 12, 19));
		widgetManager.add(new TokenSlot(widgetManager, 52, 116, 20));
		widgetManager.add(new TokenSlot(widgetManager, 136, 116, 21));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		for (int i = 0; i <= inventory.getGame().getBountyLevel() / 4; i++) {
			RenderHelper.enableGUIStandardItemLighting();
			GuiForestry.itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer,
					mc.renderEngine, LEVEL_ITEM, guiLeft + 170 + i * 8, guiTop + 7);
			RenderHelper.disableStandardItemLighting();
		}

		startPage();
		setFactor(0.5f);

		newLine();
		newLine();
		String format = EnumChatFormatting.UNDERLINE + EnumChatFormatting.ITALIC.toString();
		drawLine(format + "Attempt No. " + (NaturalistGame.BOUNTY_MAX - inventory.getGame().getBountyLevel()), 171, fontColor.get("gui.mail.lettertext"));
		newLine();
		drawSplitLine(getResearchNote(), 171, 46, fontColor.get("gui.mail.lettertext"));

		endPage();
	}

	private String getResearchNote() {
		if (lastUpdate == inventory.getGame().getLastUpdate()) {
			return researchNote;
		}

		if (!inventory.getGame().isInited()) {
			researchNote = StringUtil.localize("gui.escritoire.instructions");
		} else {
			if (inventory.getGame().isWon()) {
				researchNote = getRandomNote(researchNotes.get(Notes.success));
			} else if (inventory.getGame().isEnded()) {
				researchNote = getRandomNote(researchNotes.get(Notes.failure));
			} else {
				int bounty = inventory.getGame().getBountyLevel();
				if (bounty >= NaturalistGame.BOUNTY_MAX) {
					researchNote = getRandomNote(researchNotes.get(Notes.level1));
				} else if (bounty > NaturalistGame.BOUNTY_MAX / 2) {
					researchNote = getRandomNote(researchNotes.get(Notes.level2));
				} else if (bounty > NaturalistGame.BOUNTY_MAX / 4) {
					researchNote = getRandomNote(researchNotes.get(Notes.level3));
				} else {
					researchNote = getRandomNote(researchNotes.get(Notes.level4));
				}
			}
		}

		lastUpdate = inventory.getGame().getLastUpdate();
		return researchNote;
	}

	private String getRandomNote(List<String> candidates) {
		int index = mc.theWorld.rand.nextInt(candidates.size());
		return candidates.get(index);
	}
}
