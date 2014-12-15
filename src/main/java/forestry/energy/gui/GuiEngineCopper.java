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

import forestry.core.config.Defaults;
import forestry.energy.gadgets.EngineCopper;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiEngineCopper extends GuiEngine {

	public GuiEngineCopper(InventoryPlayer inventory, EngineCopper tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/peatengine.png", new ContainerEngineCopper(inventory, tile), tile);
	}

	protected EngineCopper getEngine() {
		return (EngineCopper)tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		EngineCopper engine = getEngine();
		int progress;
		if (engine.isBurning()) {
			progress = engine.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(guiLeft + 45, guiTop + 27 + 12 - progress, 176, 12 - progress, 14, progress + 2);
		}
	}
}
