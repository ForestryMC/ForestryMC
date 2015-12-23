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
package forestry.food.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.api.food.BeverageManager;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.food.inventory.ItemInventoryInfuser;

public class GuiInfuser extends GuiForestry<ContainerInfuser, ItemInventoryInfuser> {

	private int startX;
	private int startY;

	public GuiInfuser(InventoryPlayer inventoryplayer, ItemInventoryInfuser inventory) {
		super(Constants.TEXTURE_PATH_GUI + "/infuser.png", new ContainerInfuser(inventoryplayer, inventory), inventory);

		xSize = 176;
		ySize = 185;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		for (int l = 2; l < inventory.getSizeInventory(); l++) {
			String description = BeverageManager.ingredientManager.getDescription(inventory.getStackInSlot(l));
			if (description == null) {
				description = "(No effect)";
			}

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
