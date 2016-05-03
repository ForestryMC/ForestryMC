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

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;

import forestry.core.gui.widgets.ItemStackWidgetBase;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.factory.gui.ContainerWorktable;
import forestry.factory.recipes.RecipeMemory;

public class MemorizedRecipeSlot extends ItemStackWidgetBase {
	private static final TextureAtlasSprite lockIcon = TextureManager.getInstance().getDefault("slots/locked");
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

		GlStateManager.disableDepth();

		if (recipeMemory.isLocked(slotNumber)) {
			Proxies.render.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, lockIcon, 16, 16);
		}

		GlStateManager.enableDepth();
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if (getItemStack() != null) {
			ContainerWorktable.sendRecipeClick(mouseButton, slotNumber);
		}
	}
}
