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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.core.network.PacketId;
import forestry.core.network.PacketSocketUpdate;
import forestry.core.proxy.Proxies;
import forestry.farming.gui.ContainerFarm;
import forestry.farming.gui.GuiFarm;
import forestry.farming.multiblock.TileFarm;

public class GuiHandlerFarming extends GuiHandlerBase {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {
			case MultiFarmGUI:
				TileFarm tile = getTile(world, x, y, z, player, TileFarm.class);
				Proxies.net.sendToPlayer(new PacketSocketUpdate(PacketId.SOCKET_UPDATE, tile), player);
				return new ContainerFarm(player.inventory, tile);
			default:
				return null;

		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {
			case MultiFarmGUI:
				return new GuiFarm(player, getTile(world, x, y, z, player, TileFarm.class));
			default:
				return null;

		}
	}

}
