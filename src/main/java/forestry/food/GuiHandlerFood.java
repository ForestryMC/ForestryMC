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
package forestry.food;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.food.gui.ContainerInfuser;
import forestry.food.gui.GuiInfuser;
import forestry.food.inventory.ItemInventoryInfuser;

public class GuiHandlerFood extends GuiHandlerBase {
	@SideOnly(Side.CLIENT)
	@Override
	public Gui getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {

			case InfuserGUI:
				ItemStack infuser = player.getCurrentEquippedItem();
				if (infuser == null) {
					return null;
				}
				return new GuiInfuser(player.inventory, new ItemInventoryInfuser(player, infuser));

			default:
				return null;
		}
	}

	@Override
	public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {

			case InfuserGUI:
				ItemStack infuser = player.getCurrentEquippedItem();
				if (infuser == null) {
					return null;
				}
				return new ContainerInfuser(player.inventory, new ItemInventoryInfuser(player, infuser));

			default:
				return null;

		}
	}
}
