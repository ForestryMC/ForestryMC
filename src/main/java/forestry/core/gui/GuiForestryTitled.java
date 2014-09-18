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

import net.minecraft.util.ResourceLocation;

import forestry.core.gadgets.TileForestry;
import forestry.core.utils.StringUtil;

public abstract class GuiForestryTitled<T extends TileForestry> extends GuiForestry<T> {

	public GuiForestryTitled(String texture, ContainerForestry container, Object inventory) {
		super(texture, container, inventory);
	}

	public GuiForestryTitled(ResourceLocation texture, ContainerForestry container, Object inventory) {
		super(texture, container, inventory);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String name = StringUtil.localizeTile(tile.getUnlocalizedName());
		this.fontRendererObj.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}
}
