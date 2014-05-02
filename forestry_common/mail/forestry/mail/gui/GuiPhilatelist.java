/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.mail.gadgets.MachinePhilatelist;

public class GuiPhilatelist extends GuiForestry<MachinePhilatelist> {

	public GuiPhilatelist(InventoryPlayer player, MachinePhilatelist tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/philatelist.png", new ContainerPhilatelist(player, tile), tile);
		this.xSize = 176;
		this.ySize = 193;
	}

}
