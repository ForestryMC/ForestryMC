/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.food.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import forestry.api.food.BeverageManager;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileForestry;
import forestry.core.gui.GuiForestry;
import forestry.food.items.ItemInfuser.InfuserInventory;

public class GuiInfuser extends GuiForestry<TileForestry> {

	private final IInventory inventory;
	private int startX;
	private int startY;

	public GuiInfuser(InventoryPlayer inventoryplayer, InfuserInventory inventory) {
		super(Defaults.TEXTURE_PATH_GUI + "/infuser.png", new ContainerInfuser(inventoryplayer, inventory), inventory);

		this.inventory = inventory;

		xSize = 176;
		ySize = 185;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		for (int l = 2; l < inventory.getSizeInventory(); l++) {
			String description = BeverageManager.ingredientManager.getDescription(inventory.getStackInSlot(l));
			if (description == null)
				description = "(No effect)";

			int row = (l - 2) * 20;
			fontRendererObj.drawString(description, startX + 32, startY + 16 + row, fontColor.get("gui.screen"));
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		startX = (this.width - this.xSize) / 2;
		startY = (this.height - this.ySize) / 2;
	}
}
