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
package forestry.factory.gui.widgets;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import forestry.core.gui.widgets.ItemStackWidgetBase;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.render.TextureManager;
import forestry.factory.gui.ContainerWorktable;
import forestry.factory.recipes.RecipeMemory;

public class MemorizedRecipeSlot extends ItemStackWidgetBase {
	private static final IIcon lockIcon = TextureManager.getInstance().getDefault("slots/locked");
	private final RecipeMemory recipeMemory;
	private final int slotNumber;

	public MemorizedRecipeSlot(WidgetManager manager, int xPos, int yPos, RecipeMemory recipeMemory, int slot) {
		super(manager, xPos, yPos);
		this.recipeMemory = recipeMemory;
		this.slotNumber = slot;
	}

	@Override
	public ItemStack getItemStack() {
		return recipeMemory.getRecipeDisplayOutput(slotNumber);
	}

	@Override
	public void draw(int startX, int startY) {
		super.draw(startX, startY);

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if (recipeMemory.isLocked(slotNumber)) {
			manager.gui.setZLevel(110f);
			Proxies.render.bindTexture(SpriteSheet.ITEMS);
			manager.gui.drawTexturedModelRectFromIcon(startX + xPos, startY + yPos, lockIcon, 16, 16);
			manager.gui.setZLevel(0f);
		}

		GL11.glPopAttrib();
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if (getItemStack() != null) {
			ContainerWorktable.sendRecipeClick(mouseButton, slotNumber);
		}
	}
}
