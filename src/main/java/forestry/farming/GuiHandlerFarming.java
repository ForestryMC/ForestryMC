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
package forestry.farming;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.core.network.PacketSocketUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;
import forestry.farming.gui.ContainerFarm;
import forestry.farming.gui.GuiFarm;
import forestry.farming.tiles.TileFarm;

public class GuiHandlerFarming extends GuiHandlerBase {

	@Override
	public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {
			case MultiFarmGUI:
				TileFarm tile = TileUtil.getTile(world, x, y, z, TileFarm.class);
				Proxies.net.sendToPlayer(new PacketSocketUpdate(tile), player);
				return new ContainerFarm(player.inventory, tile);
			default:
				return null;

		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Gui getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {
			case MultiFarmGUI:
				return new GuiFarm(player, TileUtil.getTile(world, x, y, z, TileFarm.class));
			default:
				return null;

		}
	}

}
