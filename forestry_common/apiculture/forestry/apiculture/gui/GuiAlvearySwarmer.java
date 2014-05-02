/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.apiculture.gadgets.TileAlvearySwarmer;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;

public class GuiAlvearySwarmer extends GuiForestry<TileAlvearySwarmer> {

	public GuiAlvearySwarmer(InventoryPlayer inventory, TileAlvearySwarmer tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/swarmer.png", new ContainerAlvearySwarmer(inventory, tile));
	}

}
