/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.food.gui.ContainerInfuser;
import forestry.food.gui.GuiInfuser;
import forestry.food.items.ItemInfuser.InfuserInventory;

public class GuiHandlerFood extends GuiHandlerBase {
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {

		case InfuserGUI:
			return new GuiInfuser(player.inventory, new InfuserInventory(player));

		default:
			return null;
		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {

		case InfuserGUI:
			return new ContainerInfuser(player.inventory, new InfuserInventory(player));

		default:
			return null;

		}
	}
}
