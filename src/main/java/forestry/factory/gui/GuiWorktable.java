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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.buttons.GuiBetterButton;
import forestry.core.gui.buttons.StandardButtonTextureSets;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.InventoryUtil;
import forestry.factory.gui.widgets.ClearWorktable;
import forestry.factory.gui.widgets.MemorizedRecipeSlot;
import forestry.factory.inventory.InventoryGhostCrafting;
import forestry.factory.recipes.MemorizedRecipe;
import forestry.factory.recipes.RecipeMemory;
import forestry.factory.tiles.TileWorktable;

import org.lwjgl.opengl.GL11;

public class GuiWorktable extends GuiForestryTitled<ContainerWorktable, TileWorktable> {
	private static final int SPACING = 18;
	private boolean hasRecipeConflict = false;

	public GuiWorktable(EntityPlayer player, TileWorktable tile) {
		super(Constants.TEXTURE_PATH_GUI + "/worktable2.png", new ContainerWorktable(player, tile), tile);

		ySize = 218;

		RecipeMemory recipeMemory = tile.getMemory();

		int slot = 0;
		for (int y = 0; y < 3; y++) {
			int yPos = 20 + (y * SPACING);
			for (int x = 0; x < 3; x++) {
				int xPos = 110 + (x * SPACING);
				MemorizedRecipeSlot memorizedRecipeSlot = new MemorizedRecipeSlot(widgetManager, xPos, yPos, recipeMemory, slot++);
				widgetManager.add(memorizedRecipeSlot);
			}
		}

		widgetManager.add(new ClearWorktable(widgetManager, 66, 19));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (hasRecipeConflict != inventory.hasRecipeConflict()) {
			hasRecipeConflict = inventory.hasRecipeConflict();
			if (hasRecipeConflict) {
				addButtons();
			} else {
				buttonList.clear();
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float parTicks)
	{
		super.drawScreen(mouseX, mouseY, parTicks);

		final Slot itemSlot = (Slot) inventorySlots.inventorySlots.get(54 + InventoryGhostCrafting.SLOT_CRAFTING_RESULT);

		if (
			func_146978_c(itemSlot.xDisplayPosition, itemSlot.yDisplayPosition, 16, 16, mouseX, mouseY) && 
			!inventory.canTakeStack(InventoryGhostCrafting.SLOT_CRAFTING_RESULT)
		) {
			final MemorizedRecipe recipe = inventory.getCurrentRecipe();

			if (recipe != null) {
				final ItemStack[] recipeItems = InventoryUtil.getStacks(recipe.getCraftMatrix());
				final ItemStack[] availableItems = InventoryUtil.getStacks(inventory);
				final ItemStack[] ingregients = RecipeUtil.getCraftIngredients(recipeItems, availableItems);
	
				for (int slot = 0; slot < ingregients.length; slot++) {
	
					if (ingregients[slot] == null && recipeItems[slot] != null) {
						renderSlotOverlay((Slot) inventorySlots.inventorySlots.get(54 + slot));
					}
	
				}

			}

		}
 
	}

	private void renderSlotOverlay(final Slot slot) {
		final int left = guiLeft + slot.xDisplayPosition;
		final int top = guiTop + slot.yDisplayPosition;

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslatef(0, 0, 150);
		drawRect(left, top, left + 16, top + 16, 0x88ff0000);
		GL11.glTranslatef(0, 0, -150);
		GL11.glEnable(GL11.GL_LIGHTING);
    }

	@SuppressWarnings("unchecked")
	private void addButtons() {
		buttonList.add(new GuiBetterButton(0, guiLeft + 76, guiTop + 56, StandardButtonTextureSets.LEFT_BUTTON_SMALL));
		buttonList.add(new GuiBetterButton(1, guiLeft + 85, guiTop + 56, StandardButtonTextureSets.RIGHT_BUTTON_SMALL));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		int id = 100 + button.id;
		Proxies.net.sendToServer(new PacketGuiSelectRequest(id, 0));
	}
}
