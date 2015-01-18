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
package forestry.apiculture.gui;

import forestry.apiculture.gadgets.TileAlvearySieve;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiAlvearySieve extends GuiForestry<TileAlvearySieve> {

	public GuiAlvearySieve(InventoryPlayer inventory, TileAlvearySieve tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/sieve.png", new ContainerAlvearySieve(inventory, tile));
	}


}
