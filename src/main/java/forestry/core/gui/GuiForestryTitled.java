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
package forestry.core.gui;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.StatCollector;

import forestry.core.tiles.ITitled;

public abstract class GuiForestryTitled<C extends Container, I extends ITitled & IInventory> extends GuiForestry<C, I> {

	protected GuiForestryTitled(String texture, C container, I inventory) {
		super(texture, container, inventory);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

		String name = StatCollector.translateToLocal(inventory.getUnlocalizedTitle());
		textLayout.line = 6;
		textLayout.drawCenteredLine(name, 0, fontColor.get("gui.title"));
		bindTexture(textureFile);
	}
}
