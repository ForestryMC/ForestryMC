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

import forestry.core.interfaces.ITitled;
import forestry.core.utils.StringUtil;

public abstract class GuiForestryTitled<C extends Container, I extends ITitled & IInventory> extends GuiForestry<C, I> {

	public GuiForestryTitled(String texture, C container, I inventory) {
		super(texture, container, inventory);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String name = StringUtil.localizeTile(inventory.getUnlocalizedTitle());
		this.fontRendererObj.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}
}
