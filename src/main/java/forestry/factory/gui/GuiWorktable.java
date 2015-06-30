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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.WidgetManager;
import forestry.core.gui.widgets.Widget;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.render.TextureManager;
import forestry.factory.gadgets.TileWorktable;

public class GuiWorktable extends GuiForestryTitled<ContainerWorktable, TileWorktable> {

	private class MemorizedSlot extends Widget {

		private final int slotNumber;
		private final World world;

		public MemorizedSlot(World world, WidgetManager manager, int xPos, int yPos, int slot) {
			super(manager, xPos, yPos);
			this.slotNumber = slot;
			this.world = world;
		}

		private ItemStack getOutputStack() {
			return inventory.getMemory().getRecipeOutput(world, slotNumber);
		}

		@Override
		public void draw(int startX, int startY) {
			ItemStack output = getOutputStack();
			if (output != null) {
				manager.gui.drawItemStack(output, startX + xPos, startY + yPos);
			}

			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			if (inventory.getMemory().isLocked(slotNumber)) {
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
			ContainerWorktable.sendRecipeClick(mouseButton, slotNumber);
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

	public GuiWorktable(EntityPlayer player, TileWorktable tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/worktable2.png", new ContainerWorktable(player, tile), tile);

		ySize = 218;

		final int spacing = 18;
		int slot = 0;
		for (int y = 0; y < 3; y++) {
			int yPos = 20 + (y * spacing);
			for (int x = 0; x < 3; x++) {
				int xPos = 110 + (x * spacing);
				widgetManager.add(new MemorizedSlot(player.getEntityWorld(), widgetManager, xPos, yPos, slot));
				slot += 1;
			}
		}

		widgetManager.add(new ClearWorktable(widgetManager, 66, 19));
	}

}
