/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.utils.StringUtil;
import forestry.mail.gadgets.MachineTrader;

public class GuiTrader extends GuiForestry<MachineTrader> {

	private final ContainerTrader container;

	public GuiTrader(InventoryPlayer inventoryplayer, MachineTrader tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/mailtrader.png", new ContainerTrader(inventoryplayer, tile), tile);
		this.xSize = 226;
		this.ySize = 220;

		this.container = (ContainerTrader) inventorySlots;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String name = StringUtil.localize("tile.for." + tile.getInventoryName());
		this.fontRendererObj.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.mail.text"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		fontRendererObj.drawString(container.getMoniker(), guiLeft + 19, guiTop + 22, fontColor.get("gui.mail.text"));

	}
}
