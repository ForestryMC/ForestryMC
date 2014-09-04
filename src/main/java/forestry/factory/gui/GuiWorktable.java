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
package forestry.factory.gui;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.WidgetManager;
import forestry.core.gui.widgets.Widget;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;
import forestry.factory.gadgets.TileWorktable;

public class GuiWorktable extends GuiForestry<TileWorktable> {

	private class MemorizedSlot extends Widget {

		int slotNumber;

		public MemorizedSlot(WidgetManager manager, int xPos, int yPos, int slot) {
			super(manager, xPos, yPos);
			this.slotNumber = slot;
		}

		private ItemStack getOutputStack() {
			return worktable.getMemory().getRecipeOutput(slotNumber);
		}

		@Override
		public void draw(int startX, int startY) {
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableGUIStandardItemLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			ItemStack output = getOutputStack();
			if (output != null)
				manager.gui.drawItemStack(output, startX + xPos, startY + yPos);
			//RenderHelper.disableStandardItemLighting();

			GL11.glDisable(GL11.GL_DEPTH_TEST);

			if (worktable.getMemory().isLocked(slotNumber)) {
				manager.gui.setZLevel(110f);
				Proxies.common.bindTexture(SpriteSheet.ITEMS);
				manager.gui.drawTexturedModelRectFromIcon(startX + xPos, startY + yPos, TextureManager.getInstance().getDefault("slots/locked"), 16, 16);
				manager.gui.setZLevel(0f);
			}

			GL11.glPopAttrib();
		}

		@Override
		protected String getLegacyTooltip(EntityPlayer player) {
			ItemStack output = getOutputStack();
			return output != null ? output.getDisplayName() : null;
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			container.sendRecipeClick(mouseButton, slotNumber);
		}
	}

	private class BookSlot extends Widget {

		private final ItemStack BOOK = new ItemStack(Items.book);

		public BookSlot(WidgetManager manager, int xPos, int yPos) {
			super(manager, xPos, yPos);
		}

		@Override
		public void draw(int startX, int startY) {
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableGUIStandardItemLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			manager.gui.drawItemStack(BOOK, startX + xPos, startY + yPos);

			GL11.glPopAttrib();
		}

		@Override
		protected String getLegacyTooltip(EntityPlayer player) {
			return StringUtil.localize("gui.worktable.clear");
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			container.sendRecipeClick(mouseButton, 9);
		}
	}
	private final TileWorktable worktable;
	protected ContainerWorktable container;

	public GuiWorktable(EntityPlayer player, TileWorktable tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/worktable.png", new ContainerWorktable(player, tile));

		ySize = 218;
		worktable = tile;
		this.tile = tile;
		container = (ContainerWorktable) inventorySlots;

		widgetManager.add(new BookSlot(widgetManager, 128, 38));

		widgetManager.add(new MemorizedSlot(widgetManager, 146, 20, 0));
		widgetManager.add(new MemorizedSlot(widgetManager, 146, 38, 1));
		widgetManager.add(new MemorizedSlot(widgetManager, 146, 56, 2));

		widgetManager.add(new MemorizedSlot(widgetManager, 128, 56, 3));
		widgetManager.add(new MemorizedSlot(widgetManager, 110, 56, 4));

		widgetManager.add(new MemorizedSlot(widgetManager, 110, 38, 5));
		widgetManager.add(new MemorizedSlot(widgetManager, 110, 20, 6));
		widgetManager.add(new MemorizedSlot(widgetManager, 128, 20, 7));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String name = StringUtil.localizeTile(tile.getInventoryName());
		this.fontRendererObj.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}
}
