/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.storage.gui;

import forestry.core.config.Defaults;
import forestry.core.gadgets.TileForestry;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.GuiForestry;

public class GuiBackpack extends GuiForestry<TileForestry> {

	public GuiBackpack(ContainerForestry container) {
		this(Defaults.TEXTURE_PATH_GUI + "/backpack.png", container);
	}

	protected GuiBackpack(String texture, ContainerForestry container) {
		super(texture, container);
	}

	@Override
	protected boolean checkHotbarKeys(int key) {
		return false;
	}
}
