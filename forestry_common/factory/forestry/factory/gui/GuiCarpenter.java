/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.factory.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.utils.StringUtil;
import forestry.factory.gadgets.MachineCarpenter;

public class GuiCarpenter extends GuiForestry<MachineCarpenter> {

	public GuiCarpenter(InventoryPlayer inventory, MachineCarpenter tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/carpenter.png", new ContainerCarpenter(inventory, tile), tile);
		this.ySize = 218;

		widgetManager.add(new TankWidget(this.widgetManager, 150, 17, 0));
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		inventorySlots.onContainerClosed(mc.thePlayer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String name = StringUtil.localize("tile.for." + tile.getInventoryName());
		this.fontRendererObj.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		MachineCarpenter machine = tile;

		if (machine.isWorking()) {
			int progressScaled = 16 - machine.getCraftingProgressScaled(16);
			drawTexturedModalRect(guiLeft + 98, guiTop + 51 + 16 - progressScaled, 176, 60 + 16 - progressScaled, 4, progressScaled);
		}
	}

}
