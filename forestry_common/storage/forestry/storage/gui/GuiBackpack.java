/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
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
