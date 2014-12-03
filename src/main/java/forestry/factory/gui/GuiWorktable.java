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

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.WidgetManager;
import forestry.core.gui.widgets.Widget;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.render.TextureManager;
import forestry.factory.gadgets.TileWorktable;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class GuiWorktable extends GuiForestryTitled<TileWorktable> {

	private class MemorizedSlot extends Widget {

		private final int slotNumber;

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
			GL11.glEnable(GL11.GL_BLEND);

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

	private class ClearWorktable extends Widget {

		public ClearWorktable(WidgetManager manager, int xPos, int yPos) {
			super(manager, xPos, yPos);
			width = 7;
			height = 7;
		}

		@Override
		public void draw(int startX, int startY) {
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			container.clearRecipe();
		}
	}
	private final TileWorktable worktable;
	protected final ContainerWorktable container;

	public GuiWorktable(EntityPlayer player, TileWorktable tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/worktable2.png", new ContainerWorktable(player, tile), tile);

		ySize = 218;
		worktable = tile;
		container = (ContainerWorktable) inventorySlots;

		final int spacing = 18;
		int slot = 0;
		for (int y = 0; y < 3; y++) {
			int yPos = 20 + (y * spacing);
			for (int x = 0; x < 3; x++) {
				int xPos = 110 + (x * spacing);
				widgetManager.add(new MemorizedSlot(widgetManager, xPos, yPos, slot));
				slot += 1;
			}
		}

		widgetManager.add(new ClearWorktable(widgetManager, 66, 19));
	}

}
