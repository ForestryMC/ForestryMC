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

import forestry.core.render.ColourProperties;
import forestry.core.tiles.ITitled;
import forestry.core.utils.Translator;

public abstract class GuiForestryTitled<C extends Container> extends GuiForestry<C> {
	private final String unlocalizedTitle;

	protected GuiForestryTitled(String texture, C container, ITitled titled) {
		super(texture, container);
		this.unlocalizedTitle = titled.getUnlocalizedTitle();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

		String name = Translator.translateToLocal(this.unlocalizedTitle);
		textLayout.line = 6;
		if (centeredTitle()) {
			textLayout.drawCenteredLine(name, 0, ColourProperties.INSTANCE.get("gui.title"));
		} else {
			textLayout.drawLine(name, 8, ColourProperties.INSTANCE.get("gui.title"));
		}
		bindTexture(textureFile);
	}

	protected boolean centeredTitle() {
		return true;
	}
}
