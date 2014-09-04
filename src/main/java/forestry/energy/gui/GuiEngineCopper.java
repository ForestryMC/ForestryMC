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
package forestry.energy.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Defaults;
import forestry.core.utils.StringUtil;
import forestry.energy.gadgets.EngineCopper;

public class GuiEngineCopper extends GuiEngine {

	EngineCopper tile;

	public GuiEngineCopper(InventoryPlayer inventory, EngineCopper tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/peatengine.png", new ContainerEngineCopper(inventory, tile), tile);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String name = StringUtil.localizeTile(tile.getInventoryName());
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
