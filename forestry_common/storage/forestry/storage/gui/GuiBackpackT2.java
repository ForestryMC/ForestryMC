/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.storage.gui;


import forestry.core.config.Defaults;
import forestry.core.gui.ContainerForestry;

public class GuiBackpackT2 extends GuiBackpack {

	public GuiBackpackT2(ContainerForestry container) {
		super(Defaults.TEXTURE_PATH_GUI + "/backpackT2.png", container);

		xSize = 176;
		ySize = 192;
	}
}
