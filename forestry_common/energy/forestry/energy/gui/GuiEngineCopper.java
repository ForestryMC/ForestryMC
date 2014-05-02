/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.energy.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Defaults;
import forestry.core.utils.StringUtil;
import forestry.energy.gadgets.EngineCopper;

public class GuiEngineCopper extends GuiEngine {

	public GuiEngineCopper(InventoryPlayer inventory, EngineCopper tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/peatengine.png", new ContainerEngineCopper(inventory, tile), tile);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String name = StringUtil.localize("tile.for.engine.1");
		this.fontRendererObj.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
		this.fontRendererObj.drawString("Inventory", 8, this.ySize - 96 + 2, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		EngineCopper engine = (EngineCopper) tile;

		int progress;
		if (engine.isBurning()) {
			progress = engine.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(guiLeft + 45, guiTop + 27 + 12 - progress, 176, 0 + 12 - progress, 14, progress + 2);
		}
	}
}
